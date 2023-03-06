package com.todolist.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.todolist.component.DataManager;
import com.todolist.entity.Task;
import com.todolist.exceptions.NotFoundException;
import com.todolist.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {


    private final TaskRepository taskRepository;
    private final DataManager dataManager;

    @Autowired
    public TaskService(TaskRepository taskRepository, DataManager dataManager) {
        this.taskRepository = taskRepository;
        this.dataManager = dataManager;
    }

    @PostConstruct
    @Autowired
    public void loadData() throws IOException {
        List<Task> tasks = dataManager.loadTask();
        taskRepository.saveAll(tasks);
    }


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
