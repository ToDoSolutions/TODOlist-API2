package com.todolist.resources;

import com.todolist.parsers.TaskParser;
import com.todolist.entity.Task;
import com.todolist.model.ShowTask;
import com.todolist.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class TaskResource {

    @Autowired
    @Qualifier("taskRepository")
    private TaskRepository repository;

    @Autowired
    @Qualifier("taskParser")
    private TaskParser taskParser;

    @GetMapping("/notas")
    public List<ShowTask> getAllTasks() {

        return taskParser.parseList(repository.findAll());
    }

    @GetMapping("/nota")
    public List<ShowTask> getAllTasks(Pageable pageable) {
        return taskParser.parseList(repository.findAll(pageable).getContent());
    }

    @PutMapping("/task")
    public boolean addTask(@RequestBody @Valid Task task) {
        try {
            if (task.getTitle() == null) throw new IllegalArgumentException("");
            repository.save(task);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @PostMapping("/task")
    public boolean updateTask(@RequestBody @Valid Task task) {
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
