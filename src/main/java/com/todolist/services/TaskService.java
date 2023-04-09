package com.todolist.services;

import com.todolist.dtos.autodoc.RoleStatus;
import com.todolist.dtos.autodoc.clockify.ClockifyTask;
import com.todolist.entity.Group;
import com.todolist.entity.Tag;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.exceptions.NotFoundException;
import com.todolist.repositories.TaskRepository;
import lombok.AllArgsConstructor;
import org.kohsuke.github.GHIssue;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskService {

    // Services ---------------------------------------------------------------
    private final RoleService roleService;
    private final TagService tagService;

    // Repositories -----------------------------------------------------------
    private final TaskRepository taskRepository;

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
        Task task = findAllTasks().stream().filter(t -> t.getTitleIssue().equals(issue.getTitle())).findFirst()
                .orElse(new Task(issue.getTitle(), issue.getBody()));
        RoleStatus roleStatus = RoleStatus.parseTag(clockifyTask.getTagIds().stream().map(tagId -> tagService.getTagFromClockify(group, tagId))
                .filter(tag -> tag.getName() != null).findFirst().orElse(new Tag()));
        task.setUser(user);
        saveTask(task);
        roleService.saveRole(roleStatus, clockifyTask.getTimeInterval(), task);
    }

    @Transactional
    public void deleteTask(Task task) {
        roleService.deleteAllRoles(task);
        taskRepository.delete(task);
    }
}
