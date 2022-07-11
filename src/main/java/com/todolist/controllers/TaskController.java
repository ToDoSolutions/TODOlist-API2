package com.todolist.controllers;

import com.todolist.dtos.Difficulty;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.Status;
import com.todolist.entity.Task;
import com.todolist.repository.Repositories;
import com.todolist.utilities.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tasks")
@Validated
public class TaskController {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    @Autowired
    @Qualifier("repositories")
    private Repositories repositories;

    @GetMapping
    public List<Map<String, Object>> getAllTasks(@RequestParam(defaultValue = "0") @Min(value = 0, message = "The offset must be positive.") Integer offset,
                                                 @RequestParam(defaultValue = "-1") @Min(value = -1, message = "The limit must be positive") Integer limit,
                                                 @RequestParam(defaultValue = "idTask") String order,
                                                 @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES) String fields,
                                                 @RequestParam(required = false) String title,
                                                 @RequestParam(required = false) String description,
                                                 @RequestParam(required = false) @Pattern(regexp = "DRAFT|IN_PROGRESS|DONE|IN_REVISION|CANCELLED", message = "The status is invalid.") String status,
                                                 @RequestParam(required = false) @Pattern(regexp = "[<>=]{2}\\d{4}-\\d{2}-\\d{2}|[<>=]\\d{4}-\\d{2}-\\d{2}", message = "The finishedDate is invalid.") String finishedDate,
                                                 @RequestParam(required = false) @Pattern(regexp = "[<>=]{2}\\d{4}-\\d{2}-\\d{2}|[<>=]\\d{4}-\\d{2}-\\d{2}", message = "The startDate is invalid.") String startDate,
                                                 @RequestParam(required = false) String annotation,
                                                 @RequestParam(required = false) @Pattern(regexp = "[<>=]{2}\\d+|[<>=]\\d+", message = "The priority is invalid.") String priority,
                                                 @RequestParam(required = false) String difficulty,
                                                 @RequestParam(required = false) @Pattern(regexp = "[<>=]{2}\\d{4}-\\d{2}-\\d{2}|[<>=]\\d{4}-\\d{2}-\\d{2}", message = "The priority is invalid.") String duration) {
        String propertyOrder = order.charAt(0) == '+' || order.charAt(0) == '-' ? order.substring(1) : order;
        if (Arrays.stream(ShowTask.ALL_ATTRIBUTES.split(",")).noneMatch(prop -> prop.equalsIgnoreCase(propertyOrder)))
            throw new IllegalArgumentException("The order is invalid.|/api/v1/tasks");
        if (!Arrays.stream(fields.split(",")).allMatch(field -> ShowTask.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase())))
            throw new IllegalArgumentException("The fields are invalid.|/api/v1/tasks");
        List<ShowTask> result = new ArrayList<>(), tasks = repositories.findAllShowTasks(Sort.by(order.charAt(0) == '-' ? Sort.Direction.DESC : Sort.Direction.ASC, propertyOrder));
        Status auxStatus = status != null ? Status.valueOf(status.toUpperCase()) : null;
        Difficulty auxDifficulty = difficulty != null ? Difficulty.valueOf(difficulty.toUpperCase()) : null;
        if (limit == -1) limit = tasks.size() - 1;
        int start = offset == null || offset < 1 ? 0 : offset - 1; // Donde va a comenzar.
        int end = limit > tasks.size() ? tasks.size() - 1 : start + limit; // Donde va a terminar.
        for (int i = start; i < end; i++) {
            ShowTask task = tasks.get(i);
            if (task != null &&
                    (title == null || task.getTitle().contains(title)) &&
                    (auxStatus == null || task.getStatus() == auxStatus) &&
                    (startDate == null || Filter.isGEL(task.getStartDate(), startDate)) &&
                    (finishedDate == null || Filter.isGEL(task.getFinishedDate(), finishedDate)) &&
                    (priority == null || Filter.isGEL((long) task.getPriority(), priority)) &&
                    (auxDifficulty == null || task.getDifficulty() == auxDifficulty) &&
                    (duration == null || Filter.isGEL(task.getDuration(), duration)) &&
                    (annotation == null || task.getAnnotation().contains(annotation)) &&
                    (description == null || task.getDescription().contains(description)))
                result.add(task);
        }
        return result.stream().map(task -> task.getFields(fields)).collect(Collectors.toList());
    }


    @GetMapping("/{idTask}")
    public Map<String, Object> getTask(@PathVariable("idTask") @Min(value = 0, message = "The idTask must be positive.") Long idTask,
                                       @RequestParam(defaultValue = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration") String fields) {
        Task task = repositories.findTaskById(idTask);
        if (task == null)
            throw new NullPointerException("The task with idTask " + idTask + " does not exist.|/api/v1/tasks/" + idTask);
        if (!Arrays.stream(fields.split(",")).allMatch(field -> ShowTask.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase())))
            throw new IllegalArgumentException("The fields are invalid.|/api/v1/tasks/" + idTask);

        return new ShowTask(task).getFields(fields);
    }

    @PostMapping
    public Map<String, Object> addTask(@RequestBody @Valid Task task) {
        if (task.getTitle() == null)
            throw new IllegalArgumentException("The task with idTask " + task.getIdTask() + " must have title.|/api/v1/tasks/");
        if (task.getDescription() == null)
            throw new IllegalArgumentException("The task with idTask " + task.getIdTask() + " must have description.|/api/v1/tasks/");
        if (task.getFinishedDate() == null)
            throw new IllegalArgumentException("The task with idTask " + task.getIdTask() + " must have finishedDate.|/api/v1/tasks/");
        task = repositories.saveTask(task);
        ShowTask showTask = new ShowTask(task);
        if (!showTask.getStartDate().isBefore(showTask.getFinishedDate())) {
            throw new IllegalArgumentException("The startDate is must be before the finishedDate.|/api/v1/tasks");
        } else if (showTask.getFinishedDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("The finishedDate is must be after the current date.|/api/v1/tasks");
        }
        return showTask.getFields(ShowTask.ALL_ATTRIBUTES);
    }

    @PutMapping
    public Map<String, Object> updateTask(@RequestBody Task task) {
        Task oldTask = repositories.findTaskById(task.getIdTask());
        if (oldTask == null)
            throw new NullPointerException("The task with idTask " + task.getIdTask() + " does not exist.|/api/v1/tasks/" + task.getIdTask());
        if (task.getTitle() != null)
            oldTask.setTitle(task.getTitle());
        if (task.getDescription() != null)
            oldTask.setDescription(task.getDescription());
        if (task.getStatus() != null)
            oldTask.setStatus(task.getStatus());
        if (task.getFinishedDate() != null)
            oldTask.setFinishedDate(task.getFinishedDate());
        if (task.getStartDate() != null)
            oldTask.setStartDate(task.getStartDate());
        if (task.getAnnotation() != null)
            oldTask.setAnnotation(task.getAnnotation());
        if (task.getPriority() != null)
            oldTask.setPriority(task.getPriority());
        if (task.getDifficulty() != null)
            oldTask.setDifficulty(task.getDifficulty());
        Set<ConstraintViolation<Task>> errors = validator.validate(oldTask);
        if (!errors.isEmpty())
            throw new ConstraintViolationException(errors);
        ShowTask showTask = new ShowTask(oldTask);
        if (!showTask.getStartDate().isBefore(showTask.getFinishedDate()))
            throw new IllegalArgumentException("The startDate is must be before the finishedDate.|/api/v1/tasks");
        else if (showTask.getFinishedDate().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("The finishedDate is must be after the current date.|/api/v1/tasks");
        oldTask = repositories.saveTask(oldTask);
        showTask = new ShowTask(oldTask);
        return showTask.getFields(ShowTask.ALL_ATTRIBUTES);
    }


    @DeleteMapping("/{idTask}")
    public Map<String, Object> deleteTask(@PathVariable("idTask") Long idTask) {
        Task task = repositories.findTaskById(idTask);
        if (task == null)
            throw new NullPointerException("The task with idTask " + idTask + " does not exist.|/api/v1/tasks/" + idTask);
        repositories.deleteTask(task);
        return new ShowTask(task).getFields(ShowTask.ALL_ATTRIBUTES);
    }
}
