package com.todolist.services;

import com.todolist.dtos.autodoc.RoleStatus;
import com.todolist.dtos.autodoc.clockify.ClockifyTask;
import com.todolist.dtos.autodoc.clockify.TimeInterval;
import com.todolist.entity.Group;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.exceptions.NotFoundException;
import com.todolist.repositories.TaskRepository;
import org.kohsuke.github.GHIssue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
public class TaskService {

    // Services ---------------------------------------------------------------
    private final RoleService roleService;
    private final TagService tagService;

    // Repositories -----------------------------------------------------------
    private final TaskRepository taskRepository;

    // Constructors -----------------------------------------------------------
    @Autowired
    public TaskService(RoleService roleService, TagService tagService, TaskRepository taskRepository) {
        this.roleService = roleService;
        this.tagService = tagService;
        this.taskRepository = taskRepository;
    }

    // Finders ----------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<Task> findAllTasks(Sort sort) {
        return taskRepository.findAll(sort);
    }

    @Transactional(readOnly = true)
    public List<Task> findAllTasks() {
        return taskRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Task findTaskById(Integer idTask) {
        return taskRepository.findById(idTask).orElseThrow(() -> new NotFoundException("The task with idTask " + idTask + " does not exist."));
    }

    // Save and delete --------------------------------------------------------
    @Transactional
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    @Transactional
    public void saveTask(GHIssue issue, ClockifyTask clockifyTask, Group group, User user) {
        Task task = findAllTasks().stream().filter(t -> t.getTitleIssue().equals(issue.getTitle())).findFirst().orElse(new Task(issue.getTitle(), issue.getBody()));
        RoleStatus roleStatus;
        if (Objects.isNull(clockifyTask.getTagIds()))
            roleStatus = RoleStatus.UNDEFINED;
        else
            roleStatus = RoleStatus.parseTag(clockifyTask.getTagIds().stream().map(tagId -> tagService.getTagFromClockify(group, tagId)).findFirst().orElse(null));
        task.setUser(user);
        TimeInterval timeInterval = clockifyTask.getTimeInterval();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        LocalDateTime start = LocalDateTime.parse(timeInterval.getStart(), formatter);
        LocalDateTime end = LocalDateTime.parse(timeInterval.getEnd(), formatter);
        saveTask(task);
        roleService.saveRole(roleStatus, start, end, task);
    }

    @Transactional
    public void deleteTask(Task task) {
        roleService.deleteAllRoles(task);
        taskRepository.delete(task);
    }
}
