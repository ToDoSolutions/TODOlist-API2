package com.todolist.controllers;

import com.google.common.base.Preconditions;
import com.todolist.dtos.Difficulty;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.Status;
import com.todolist.entity.Task;
import com.todolist.services.TaskService;
import com.todolist.utilities.Filter;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/v1/tasks")
@Validated
@AllArgsConstructor
public class TaskController {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator(); // Arreglar algún día.

    private TaskService taskService;

    @GetMapping
    public List<Map<String, Object>> getAllTasks(@RequestParam(defaultValue = "0") @Min(value = 0, message = "The offset must be positive.") Integer offset,
                                                 @RequestParam(defaultValue = "-1") @Min(value = -1, message = "The limit must be positive") Integer limit,
                                                 @RequestParam(defaultValue = "idTask") String order,
                                                 @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES) String fields,
                                                 @RequestParam(required = false) String title,
                                                 @RequestParam(required = false) String description,
                                                 @RequestParam(required = false) /*@Pattern(regexp = "[Dd][Rr][Aa][Ff][Tt]|[Ii][Nn][_ ][Pp][Rr][Oo][Gg][Rr][Ee][Ss][Ss]|[Dd][Oo][Nn][Ee]|[Ii][Nn][_ ][Rr][Ee][Vv][Ii][Ss][Ii][Oo][Nn]|[Cc][Aa][Nn][Cc][Ee][Ll][Ll][Ee][Dd]", message = "The status is invalid.")*/ String status,
                                                 @RequestParam(required = false) @Pattern(regexp = "[<>=]{2}\\d{4}-\\d{2}-\\d{2}|[<>=]\\d{4}-\\d{2}-\\d{2}", message = "The finishedDate is invalid.") String finishedDate,
                                                 @RequestParam(required = false) @Pattern(regexp = "[<>=]{2}\\d{4}-\\d{2}-\\d{2}|[<>=]\\d{4}-\\d{2}-\\d{2}", message = "The startDate is invalid.") String startDate,
                                                 @RequestParam(required = false) String annotation,
                                                 @RequestParam(required = false) @Pattern(regexp = "[<>=]{2}\\d+|[<>=]\\d+", message = "The priority is invalid.") String priority,
                                                 @RequestParam(required = false) /*@Pattern(regexp = "[Ss][Ll][Ee][Ee][Pp]|[Ee][Aa][Ss][Yy]|[Mm][Ee][Dd][Ii][Uu][Mm]|[Hh][Aa][Rr][Dd]|[Hh][Aa][Rr][Dd][Cc][Oo][Rr][Ee]|[Ii][_ ][Ww][Aa][Nn][Tt][_ ][Tt][Oo][_ ][Dd][Ii][Ee]", message = "The difficulty is invalid.")*/ String difficulty,
                                                 @RequestParam(required = false) @Pattern(regexp = "[<>=]{2}\\d{4}-\\d{2}-\\d{2}|[<>=]\\d{4}-\\d{2}-\\d{2}", message = "The priority is invalid.") String duration) {
        String propertyOrder = order.charAt(0) == '+' || order.charAt(0) == '-' ? order.substring(1) : order;
        Preconditions.checkArgument(Arrays.stream(ShowTask.ALL_ATTRIBUTES.split(",")).anyMatch(prop -> prop.equalsIgnoreCase(propertyOrder)), "The order is invalid.");
        Preconditions.checkArgument(Arrays.stream(fields.split(",")).allMatch(field -> ShowTask.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase())), "The fields are invalid.");
        List<ShowTask> result = new ArrayList<>(), tasks = taskService.findAllShowTasks(Sort.by(order.charAt(0) == '-' ? Sort.Direction.DESC : Sort.Direction.ASC, propertyOrder));
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
        return result.stream().map(task -> task.getFields(fields)).toList();
    }


    @GetMapping("/{idTask}")
    public Map<String, Object> getTask(@PathVariable("idTask") @Min(value = 0, message = "The idTask must be positive.") Long idTask,
                                       @RequestParam(defaultValue = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration") String fields) {
        Task task = taskService.findTaskById(idTask);
        Preconditions.checkNotNull(task, "The task with idTask " + idTask + " does not exist.");
        Preconditions.checkArgument(Arrays.stream(fields.split(",")).allMatch(field -> ShowTask.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase())), "The fields are invalid.");
        return new ShowTask(task).getFields(fields);
    }

    @PostMapping
    public Map<String, Object> addTask(@RequestBody @Valid Task task) {
        Preconditions.checkNotNull(task, "The task is null.|/api/v1/tasks");
        Preconditions.checkNotNull(task.getTitle(), "The task with idTask " + task.getIdTask() + " must have title.");
        Preconditions.checkNotNull(task.getDescription(), "The task with idTask " + task.getIdTask() + " must have description.");
        Preconditions.checkNotNull(task.getFinishedDate(), "The task with idTask " + task.getIdTask() + " must have finishedDate.");
        task = taskService.saveTask(task);
        ShowTask showTask = new ShowTask(task);
        Preconditions.checkArgument(showTask.getStartDate().isBefore(showTask.getFinishedDate()), "The startDate is must be before the finishedDate.");
        Preconditions.checkArgument(!showTask.getFinishedDate().isBefore(LocalDate.now()), "The finishedDate is must be after the current date.");
        return showTask.getFields(ShowTask.ALL_ATTRIBUTES);
    }

    @PutMapping
    public Map<String, Object> updateTask(@RequestBody Task task) {
        Task oldTask = taskService.findTaskById(task.getIdTask());
        if (oldTask == null)
            throw new NullPointerException("The task with idTask " + task.getIdTask() + " does not exist." + task.getIdTask());
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
        Preconditions.checkArgument(showTask.getStartDate().isBefore(showTask.getFinishedDate()), "The startDate is must be before the finishedDate.");
        Preconditions.checkArgument(!showTask.getFinishedDate().isBefore(LocalDate.now()), "The finishedDate is must be after the current date.");
        oldTask = taskService.saveTask(oldTask);
        showTask = new ShowTask(oldTask);
        return showTask.getFields(ShowTask.ALL_ATTRIBUTES);
    }


    @DeleteMapping("/{idTask}")
    public Map<String, Object> deleteTask(@PathVariable("idTask") Long idTask) {
        Task task = taskService.findTaskById(idTask);
        Preconditions.checkNotNull(task, "The task with idTask " + idTask + " does not exist.");
        taskService.deleteTask(task);
        return new ShowTask(task).getFields(ShowTask.ALL_ATTRIBUTES);
    }
}
