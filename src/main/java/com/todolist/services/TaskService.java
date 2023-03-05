package com.todolist.services;

import com.todolist.entity.Task;
import com.todolist.exceptions.NotFoundException;
import com.todolist.repositories.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class TaskService {


    private TaskRepository taskRepository;


    @Transactional(readOnly = true)
    public List<Task> findAllTasks(Sort sort) {
        return taskRepository.findAll(sort);
    }

    @Transactional(readOnly = true)
    public List<Task> findAllTasks() {
        return taskRepository.findAll().stream().toList();
    }

    @Transactional
    public Task findTaskById(Long idTask) {
        return taskRepository.findById(idTask).orElseThrow(() -> new NotFoundException("The task with idTask " + idTask + " does not exist."));
    }

    @Transactional(readOnly = true)
    public Task findTaskByTitle(String title) {
        return taskRepository.findByTitle(title).orElseThrow(() -> new NotFoundException("The task with title " + title + " does not exist."));
    }

    @Transactional
    public Task saveTask(Task task) {
        if (task.getStartDate() == null)
            task.setStartDate(LocalDate.now());
        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Task task) {
        taskRepository.delete(task);
    }

}
