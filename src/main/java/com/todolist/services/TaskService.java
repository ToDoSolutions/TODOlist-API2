package com.todolist.services;

import com.todolist.entity.Task;
import com.todolist.exceptions.NotFoundException;
import com.todolist.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    // Repositories -----------------------------------------------------------
    private final TaskRepository taskRepository;

    // Constructors -----------------------------------------------------------
    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Finders ----------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<Task> findAllTasks(Sort sort) {
        return taskRepository.findAll(sort);
    }

    @Transactional(readOnly = true)
    public List<Task> findAllTasks() {
        return taskRepository.findAll().stream().toList();
    }

    @Transactional(readOnly = true)
    public Task findTaskById(Integer idTask) {
        return taskRepository.findById(idTask).orElseThrow(() -> new NotFoundException("The task with idTask " + idTask + " does not exist."));
    }

    // Save and delete --------------------------------------------------------
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
