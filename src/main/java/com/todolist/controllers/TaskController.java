package com.todolist.controllers;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.todolist.dtos.Difficulty;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.Status;
import com.todolist.entity.Task;
import com.todolist.exceptions.BadRequestException;
import com.todolist.exceptions.NotFoundException;
import com.todolist.filters.DateFilter;
import com.todolist.filters.NumberFilter;
import com.todolist.services.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/v1/tasks")
@Validated
@AllArgsConstructor
public class TaskController {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator(); // Arreglar algún día.

    private TaskService taskService;

    @SuppressWarnings("unchecked")
    @GetMapping
    public List<Map<String, Object>> getAllTasks(@RequestParam(defaultValue = "0") @Min(value = 0, message = "The offset must be positive.") Integer offset,
                                                 @RequestParam(defaultValue = "-1") @Min(value = -1, message = "The limit must be positive.") Integer limit,
                                                 @RequestParam(defaultValue = "idTask") String order,
                                                 @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES) String fields,
                                                 @RequestParam(required = false) String title,
                                                 @RequestParam(required = false) String description,
                                                 @RequestParam(required = false) Status status,
                                                 @RequestParam(required = false) DateFilter finishedDate,
                                                 @RequestParam(required = false) DateFilter startDate,
                                                 @RequestParam(required = false) String annotation,
                                                 @RequestParam(required = false) NumberFilter priority,
                                                 @RequestParam(required = false) Difficulty difficulty,
                                                 @RequestParam(required = false) NumberFilter duration) {
        String propertyOrder = order.charAt(0) == '+' || order.charAt(0) == '-' ? order.substring(1) : order;
        List<String> listFields =  List.of(ShowTask.ALL_ATTRIBUTES.toLowerCase().split(","));
        System.out.println(listFields);
        System.out.println(propertyOrder);
        if (listFields.stream().noneMatch(prop -> prop.equalsIgnoreCase(propertyOrder)))
            throw new BadRequestException("The order is invalid.");
        if (!(Arrays.stream(fields.split(",")).allMatch(field -> listFields.contains(field.toLowerCase()))))
            throw new BadRequestException("The fields are invalid.");
        List<ShowTask> result = Lists.newArrayList(), tasks = taskService.findAllShowTasks(Sort.by(order.charAt(0) == '-' ? Sort.Direction.DESC : Sort.Direction.ASC, propertyOrder));
        if (limit == -1) limit = tasks.size();
        int start = offset == null || offset < 1 ? 0 : offset - 1; // Donde va a comenzar.
        int end = limit > tasks.size() | limit + start > tasks.size() ? limit : start + limit; // Donde va a terminar.
        System.out.println(end);
        for (int i = start; i < end; i++) {
            ShowTask task = tasks.get(i);
            System.out.println(task);
            if (task != null &&
                    (title == null || task.getTitle().contains(title)) &&
                    (status == null || task.getStatus() == status) &&
                    (startDate == null || startDate.isValid(task.getStartDate())) &&
                    (finishedDate == null || finishedDate.isValid(task.getFinishedDate())) &&
                    (priority == null || priority.isValid(task.getPriority())) &&
                    (difficulty == null || task.getDifficulty() == difficulty) &&
                    (duration == null || duration.isValid(task.getDuration())) &&
                    (annotation == null || task.getAnnotation().contains(annotation)) &&
                    (description == null || task.getDescription().contains(description)))
                result.add(task);
        }
        return result.stream().map(task -> task.getFields(fields)).toList();
    }


    @SuppressWarnings("unchecked")
    @GetMapping("/{idTask}")
    public Map<String, Object> getTask(@PathVariable("idTask") @Min(value = 0, message = "The idTask must be positive.") Long idTask,
                                       @RequestParam(defaultValue = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration") String fields) {
        Task task = taskService.findTaskById(idTask);
        if (task == null)
            throw new NotFoundException("The task with idTask " + idTask + " does not exist.");
        if (!(Arrays.stream(fields.split(",")).allMatch(field -> ShowTask.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase()))))
            throw new BadRequestException("The fields are invalid.");
        return new ShowTask(task).getFields(fields);
    }

    @PostMapping
    public Map<String, Object> addTask(@RequestBody @Valid Task task) {
        if (task == null)
            throw new BadRequestException("Task is null.");
        if (task.getTitle() == null || task.getTitle().isEmpty())
            throw new BadRequestException("The task with idTask " + task.getIdTask() + " must have title.");
        if (task.getDescription() == null || task.getDescription().isEmpty())
            throw new BadRequestException("The task with idTask " + task.getIdTask() + " must have description.");
        if (task.getFinishedDate() == null || task.getFinishedDate().isEmpty())
            throw new BadRequestException("The task with idTask " + task.getIdTask() + " must have finishedDate.");
        if (task.getStartDate() == null) task.setStartDate(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        ShowTask showTask = new ShowTask(task);
        if (!showTask.getStartDate().isBefore(showTask.getFinishedDate()))
                throw  new BadRequestException("The startDate must be before the finishedDate.");
        if (showTask.getFinishedDate().isBefore(LocalDate.now()))
                throw new BadRequestException("The finishedDate must be after the current date.");
        task = taskService.saveTask(task);
        showTask = new ShowTask(task);
        return showTask.getFields(ShowTask.ALL_ATTRIBUTES);
    }

    @PutMapping
    public Map<String, Object> updateTask(@RequestBody @Valid Task task) {
        Task oldTask = taskService.findTaskById(task.getIdTask());
        if (oldTask == null)
            throw new NotFoundException("The task with idTask " + task.getIdTask() + " does not exist.");
        if (task.getTitle() != null && !Objects.equals(task.getTitle(), ""))
            oldTask.setTitle(task.getTitle());
        if (task.getDescription() != null && !Objects.equals(task.getDescription(), ""))
            oldTask.setDescription(task.getDescription());
        if (task.getStatus() != null && !Objects.equals(task.getStatus(), ""))
            oldTask.setStatus(task.getStatus());
        if (task.getFinishedDate() != null && !Objects.equals(task.getFinishedDate(), ""))
            oldTask.setFinishedDate(task.getFinishedDate());
        if (task.getStartDate() != null && !Objects.equals(task.getStartDate(), ""))
            oldTask.setStartDate(task.getStartDate());
        if (task.getAnnotation() != null && !Objects.equals(task.getAnnotation(), ""))
            oldTask.setAnnotation(task.getAnnotation());
        if (task.getPriority() != null && !Objects.equals(task.getPriority(), ""))
            oldTask.setPriority(task.getPriority());
        if (task.getDifficulty() != null && !Objects.equals(task.getDifficulty(), ""))
            oldTask.setDifficulty(task.getDifficulty());
        Set<ConstraintViolation<Task>> errors = validator.validate(oldTask);
        if (!errors.isEmpty())
            throw new ConstraintViolationException(errors);
        ShowTask showTask = new ShowTask(oldTask);
        if (!showTask.getStartDate().isBefore(showTask.getFinishedDate()))
                throw  new BadRequestException("The startDate is must be before the finishedDate.");
        if (showTask.getFinishedDate().isBefore(LocalDate.now()))
                throw new BadRequestException("The finishedDate is must be after the current date.");
        oldTask = taskService.saveTask(oldTask);
        showTask = new ShowTask(oldTask);
        return showTask.getFields(ShowTask.ALL_ATTRIBUTES);
    }


    @DeleteMapping("/{idTask}")
    public Map<String, Object> deleteTask(@PathVariable("idTask") Long idTask) {
        Task task = taskService.findTaskById(idTask);
        if (task == null)
            throw new NotFoundException("The task with idTask " + idTask + " does not exist.");
        taskService.deleteTask(task);
        return new ShowTask(task).getFields(ShowTask.ALL_ATTRIBUTES);
    }
}
