package com.todolist.controllers;

import com.fadda.common.Preconditions;
import com.fadda.iterables.iterator.IterableRangeObject;
import com.todolist.component.DTOManager;
import com.todolist.dtos.Difficulty;
import com.todolist.dtos.Order;
import com.todolist.dtos.ShowTask;
import com.todolist.entity.Task;
import com.todolist.exceptions.BadRequestException;
import com.todolist.filters.NumberFilter;
import com.todolist.services.TaskService;
import com.todolist.validators.FieldValidator;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@RestController
@RequestMapping("/api/v1")
public class TaskController {


    // Services ---------------------------------------------------------------
    private final TaskService taskService;
    private final DTOManager dtoManager;

    private final Consumer<String[]> fieldValidator;

    public TaskController(TaskService taskService, DTOManager dtoManager, FieldValidator fieldValidator) {
        this.taskService = taskService;
        this.dtoManager = dtoManager;
        this.fieldValidator = fields -> {
            fieldValidator.taskFieldValidate(fields[0]);
            fieldValidator.userFieldValidate(fields[1]);
            fieldValidator.groupFieldValidate(fields[2]);
        };
    }

    // Methods ----------------------------------------------------------------
    @DeleteMapping("/task/{idTask}")
    public Map<String, Object> deleteTask(@PathVariable("idTask") Integer idTask) {
        Task task = taskService.findTaskById(idTask);
        taskService.deleteTask(task);
        ShowTask showTask = new ShowTask(task);
        return dtoManager.getEntityAsJson(showTask);
    }

    /* TASKS OPERATIONS */
    @GetMapping("/tasks")
    public List<Map<String, Object>> getAllTasks(@RequestParam(defaultValue = "0") @Min(value = 0, message = "The offset must be positive.") Integer offset,
                                                 @RequestParam(defaultValue = "-1") @Min(value = -1, message = "The limit must be positive.") Integer limit,
                                                 @RequestParam(defaultValue = "+idTask") Order order,
                                                 @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES_STRING) String fieldsTask,
                                                 @RequestParam(required = false) String title,
                                                 @RequestParam(required = false) String description,
                                                 @RequestParam(required = false) String annotation,
                                                 @RequestParam(required = false) NumberFilter priority,
                                                 @RequestParam(required = false) Difficulty difficulty) {
        order.validateOrder(fieldsTask);
        List<Task> tasks = taskService.findAllTasks(order.getSort());
        List<Task> result = new IterableRangeObject<>(tasks, limit, offset)
                .stream().filter(task -> Objects.nonNull(task) &&
                        Preconditions.isNullOrValid(title, t -> task.getTitle().contains(t)) &&
                        Preconditions.isNullOrValid(priority, p -> p.isValid(task.getPriority())) &&
                        Preconditions.isNullOrValid(difficulty, d -> task.getDifficulty() == d) &&
                        Preconditions.isNullOrValid(annotation, a -> task.getAnnotation().contains(a)) &&
                        Preconditions.isNullOrValid(description, d -> task.getDescription().contains(d))).toList();

        return result.stream()
                .map(ShowTask::new).map(task -> dtoManager.getEntityAsJson(task, fieldValidator, fieldsTask)).toList();
    }

    @GetMapping("/task/{idTask}") // GetSoloTest
    public Map<String, Object> getTask(@PathVariable("idTask") @Min(value = 0, message = "The idTask must be positive.") Integer idTask,
                                       @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES_STRING) String fieldsTask) {
        Task task = taskService.findTaskById(idTask);
        ShowTask showTask = new ShowTask(task);
        return dtoManager.getEntityAsJson(showTask, fieldValidator, fieldsTask);
    }

    @PostMapping("/task")
    public ResponseEntity<ShowTask> addTask(@RequestBody @Valid Task task, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new BadRequestException("The task is invalid.");
        task = taskService.saveTask(task);
        ShowTask showTask = new ShowTask(task);
        return ResponseEntity.ok(showTask);
    }

    @PutMapping("/task")
    public ResponseEntity<ShowTask> updateTask(@RequestBody @Valid Task task, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new BadRequestException("The task is invalid.");
        Task oldTask = taskService.findTaskById(task.getId());
        BeanUtils.copyProperties(task, oldTask, "idTask");
        taskService.saveTask(oldTask);
        ShowTask showTask = new ShowTask(oldTask);
        return ResponseEntity.ok(showTask);
    }
}
