package com.todolist.controllers;

import com.todolist.component.DTOManager;
import com.todolist.dtos.Difficulty;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.Status;
import com.todolist.entity.Task;
import com.todolist.exceptions.BadRequestException;
import com.todolist.filters.DateFilter;
import com.todolist.filters.NumberFilter;
import com.todolist.services.TaskService;
import com.todolist.utilities.MyIterable;
import com.todolist.utilities.Order;
import com.todolist.utilities.Predicator;
import com.todolist.validators.task.DateTaskValidator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
public class TaskController {


    private final TaskService taskService;
    private final DateTaskValidator dateTaskValidator;
    private final DTOManager dtoManager;

    @Autowired
    public TaskController(TaskService taskService, DateTaskValidator dateTaskValidator, DTOManager dtoManager) {
        this.taskService = taskService;
        this.dateTaskValidator = dateTaskValidator;
        this.dtoManager = dtoManager;
    }

    @DeleteMapping("/task/{idTask}") // DeleteTest
    public Map<String, Object> deleteTask(@PathVariable("idTask") Long idTask) {
        Task task = taskService.findTaskById(idTask);
        taskService.deleteTask(task);
        return dtoManager.getShowTaskAsJson(task);
    }

    /* TASKS OPERATIONS */

    @GetMapping("/tasks") // GetAllTest
    public List<Map<String, Object>> getAllTasks(@RequestParam(defaultValue = "0") @Min(value = 0, message = "The offset must be positive.") Integer offset,
                                                 @RequestParam(defaultValue = "-1") @Min(value = -1, message = "The limit must be positive.") Integer limit,
                                                 @RequestParam(defaultValue = "+idTask") Order order,
                                                 @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES_STRING) String fieldsTask,
                                                 @RequestParam(required = false) String title,
                                                 @RequestParam(required = false) String description,
                                                 @RequestParam(required = false) Status status,
                                                 @RequestParam(required = false) DateFilter finishedDate,
                                                 @RequestParam(required = false) DateFilter startDate,
                                                 @RequestParam(required = false) String annotation,
                                                 @RequestParam(required = false) NumberFilter priority,
                                                 @RequestParam(required = false) Difficulty difficulty,
                                                 @RequestParam(required = false) NumberFilter duration) {
        order.validateOrder(fieldsTask);
        List<Task> tasks = taskService.findAllTasks(order.getSort());
        List<Task> result = new MyIterable<>(tasks, limit, offset)
                .stream().filter(task -> Objects.nonNull(task) &&
                        Predicator.isNullOrValid(title, t -> task.getTitle().contains(t)) &&
                        Predicator.isNullOrValid(status, s -> task.getStatus() == s) &&
                        Predicator.isNullOrValid(startDate, s -> s.isValid(task.getStartDate())) &&
                        Predicator.isNullOrValid(finishedDate, f -> f.isValid(task.getFinishedDate())) &&
                        Predicator.isNullOrValid(priority, p -> p.isValid(task.getPriority())) &&
                        Predicator.isNullOrValid(difficulty, d -> task.getDifficulty() == d) &&
                        Predicator.isNullOrValid(duration, d -> d.isValid(task.getDuration())) &&
                        Predicator.isNullOrValid(annotation, a -> task.getAnnotation().contains(a)) &&
                        Predicator.isNullOrValid(description, d -> task.getDescription().contains(d))).toList();
        return result.stream().map(task -> dtoManager.getShowTaskAsJson(task, fieldsTask)).toList();
    }

    @GetMapping("/task/{idTask}") // GetSoloTest
    public Map<String, Object> getTask(@PathVariable("idTask") @Min(value = 0, message = "The idTask must be positive.") Long idTask,
                                       @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES_STRING) String fieldsTask) {
        Task task = taskService.findTaskById(idTask);
        return dtoManager.getShowTaskAsJson(task, fieldsTask);
    }

    @PostMapping("/task") // PostTest
    public Map<String, Object> addTask(@RequestBody @Valid Task task, BindingResult bindingResult) {
        dateTaskValidator.validate(task, bindingResult);
        if (bindingResult.hasErrors())
            throw new BadRequestException("The task is invalid.");
        task = taskService.saveTask(task);
        return dtoManager.getShowTaskAsJson(task);
    }

    @PutMapping("/task") // PutTest
    public Map<String, Object> updateTask(@RequestBody @Valid Task task, BindingResult bindingResult) {
        dateTaskValidator.validate(task, bindingResult);
        if (bindingResult.hasErrors())
            throw new BadRequestException("The task is invalid.");
        Task oldTask = taskService.findTaskById(task.getIdTask());
        BeanUtils.copyProperties(task, oldTask, "idTask");
        taskService.saveTask(oldTask);
        return dtoManager.getShowTaskAsJson(task);
    }
}
