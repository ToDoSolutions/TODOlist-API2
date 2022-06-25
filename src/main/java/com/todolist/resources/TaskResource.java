package com.todolist.resources;

import com.todolist.entity.Task;
import com.todolist.model.Difficulty;
import com.todolist.model.ShowTask;
import com.todolist.model.Status;
import com.todolist.parsers.TaskParser;
import com.todolist.repository.Repositories;
import com.todolist.utilities.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tasks")
@Validated
public class TaskResource {

    @Autowired
    @Qualifier("repositories")
    private Repositories repositories;

    @Autowired
    @Qualifier("taskParser")
    private TaskParser taskParser;

    @GetMapping
    public List<Map<String, Object>> getAllTasks(@RequestParam(defaultValue = "0") @Min(value = 0, message = "The offset must be positive.") Integer offset,
                                                 @RequestParam(defaultValue = Integer.MAX_VALUE + "") @Min(value = 0, message = "The limit must be positive") Integer limit,
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
        List<ShowTask> result = new ArrayList<>(),
                tasks = taskParser.parseList(repositories.taskRepository.findAll(PageRequest.of(offset, limit, Sort.by(order.charAt(0) == '-' ? Sort.Direction.DESC : Sort.Direction.ASC, order.charAt(0) == '+' || order.charAt(0) == '-' ? order.substring(1, order.length() - 1) : order))).getContent());
        Status auxStatus = status != null ? Status.valueOf(status.toUpperCase()) : null;
        Difficulty auxDifficulty = difficulty != null ? Difficulty.valueOf(difficulty.toUpperCase()) : null;
        for (ShowTask task : tasks) {
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
        Task task = repositories.taskRepository.findById(idTask).orElse(null);
        if (task == null)
            throw new NullPointerException("The task with idTask " + idTask + " does not exist.|uri=/api/v1/tasks/" + idTask);
        if (!Arrays.stream(fields.split(",")).allMatch(field -> ShowTask.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase())))

            throw new IllegalArgumentException("The fields are invalid.|uri=/api/v1/tasks/" + idTask);

        return new ShowTask(task).getFields(fields);
    }

    @PostMapping
    public Map<String, Object> addTask(@RequestBody @Valid Task task) {
        if (task.getTitle() == null)
            throw new IllegalArgumentException("The task with idTask " + task.getIdTask() + " must have title.|uri=/api/v1/tasks/");
        task = repositories.taskRepository.save(task);
        ShowTask showTask = new ShowTask(task);
        if (!showTask.getStartDate().isBefore(showTask.getFinishedDate())) {
            throw new IllegalArgumentException("The startDate is must be before the finishedDate.|uri=/api/v1/tasks");
        }
        return showTask.getFields(ShowTask.ALL_ATTRIBUTES);
    }

    @PutMapping
    public Map<String, Object> updateTask(@RequestBody Task task) {
        Task oldTask = repositories.taskRepository.findByIdTask(task.getIdTask());
        if (oldTask == null)
            throw new NullPointerException("The task with idTask " + task.getIdTask() + " does not exist.|uri=/api/v1/tasks/" + task.getIdTask());
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
        @Valid Task  validated = oldTask;
        oldTask = repositories.taskRepository.save(validated);
        ShowTask showTask = new ShowTask(oldTask);
        if (!showTask.getStartDate().isBefore(showTask.getFinishedDate())) {
            throw new IllegalArgumentException("The startDate is must be before the finishedDate.|uri=/api/v1/tasks");
        }
        return showTask.getFields(ShowTask.ALL_ATTRIBUTES);
    }


    @DeleteMapping("/{idTask}")
    public Map<String, Object> deleteTask(@PathVariable("idTask") Long idTask) {
        Task task = repositories.taskRepository.findByIdTask(idTask);
        if (task == null)
            throw new NullPointerException("The task with idTask " + idTask + " does not exist.|uri=/api/v1/tasks/" + idTask);
        repositories.taskRepository.delete(task);
        return new ShowTask(task).getFields(ShowTask.ALL_ATTRIBUTES);
    }
}
