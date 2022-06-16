package com.todolist.resources;

import com.todolist.entity.Task;
import com.todolist.model.Difficulty;
import com.todolist.model.ShowTask;
import com.todolist.model.Status;
import com.todolist.parsers.TaskParser;
import com.todolist.repository.TaskRepository;
import com.todolist.utilities.Filter;
import com.todolist.utilities.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@Validated
public class TaskResource {

    @Autowired
    @Qualifier("taskRepository")
    private TaskRepository repository;

    @Autowired
    @Qualifier("taskParser")
    private TaskParser taskParser;

    @GetMapping("/tasks")
    public List<Map<String, Object>> getAllTasks(@RequestParam(defaultValue = "0") @Min(value = 0, message = "The offset must be positive.") Integer offset,
                                                 @RequestParam(defaultValue = Integer.MAX_VALUE + "") @Min(value = 0, message = "The limit must be positive") Integer limit,
                                                 @RequestParam(defaultValue = "idTask") String order,
                                                 @RequestParam(defaultValue = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration") String fields,
                                                 @RequestParam(required = false) String title,
                                                 @RequestParam(required = false) String description,
                                                 @RequestParam(required = false) @Pattern(regexp = "DRAFT|IN_PROGRESS|DONE|IN_REVISION|CANCELLED", message = "The status is invalid.") String status,
                                                 @RequestParam(required = false) @Pattern(regexp = "[<>=]{2}\\d{4}-\\d{2}-\\d{2}|[<>=]\\d{4}-\\d{2}-\\d{2}", message = "The finishedDate is invalid.") String finishedDate,
                                                 @RequestParam(required = false) @Pattern(regexp = "[<>=]{2}\\d{4}-\\d{2}-\\d{2}|[<>=]\\d{4}-\\d{2}-\\d{2}", message = "The startDate is invalid.") String startDate,
                                                 @RequestParam(required = false) String annotation,
                                                 @RequestParam(required = false) @Pattern(regexp = "[<>=]{2}\\d+|[<>=]\\d+", message = "The priority is invalid.") String priority,
                                                 @RequestParam(required = false) String difficulty,
                                                 @RequestParam(required = false) @Pattern(regexp = "[<>=]{2}\\d{4}-\\d{2}-\\d{2}|[<>=]\\d{4}-\\d{2}-\\d{2}", message = "The priority is invalid.") String duration) {
        System.out.println(order);
        List<ShowTask> result = new ArrayList<>(),
                tasks = taskParser.parseList(repository.findAll(PageRequest.of(offset, limit, Order.sequenceTask(order))).getContent());
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

    @PostMapping("/tasks")
    public boolean addTask(@RequestBody @Valid Task task) {
        try {
            if (task.getTitle() == null) throw new IllegalArgumentException("");
            repository.save(task);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @PutMapping("/tasks")
    public boolean updateTask(@RequestBody @Valid Task task) {
        System.out.println(task.getIdTask());
        try {
            Task oldTask = repository.findByIdTask(task.getIdTask());
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
            repository.save(oldTask);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @DeleteMapping("/task/{idTask}")
    public boolean deleteTask(@PathVariable("idTask") Long idTask) {
        try {
            Task task = repository.findByIdTask(idTask);
            repository.delete(task);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
