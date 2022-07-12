package com.todolist.services;

import com.todolist.dtos.ShowTask;
import com.todolist.entity.Task;
import com.todolist.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskService {


    private TaskRepository taskRepository;

    public List<ShowTask> findAllShowTasks(Sort sort) {
        return taskRepository.findAll(sort).stream().map(ShowTask::new).toList();
    }

    public Task findTaskById(Long idTask) {
        return taskRepository.findById(idTask).orElse(null);
    }

    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(Task task) {
        taskRepository.delete(task);
    }
}
