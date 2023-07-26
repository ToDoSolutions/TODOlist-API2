package com.todolist.services;

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
import java.util.Optional;
import java.util.regex.Pattern;

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
        Task task = findAllTasks().stream()
                .filter(t -> t.getTitleIssue().equals(issue.getTitle()))
                .findFirst()
                .orElseGet(() -> new Task(issue.getTitle(), issue.getBody()));
        task.setUser(user);
        saveTask(task);
        roleService.saveRole(getTag(clockifyTask, group).getName(), clockifyTask.getTimeInterval(), task);
    }

    private Tag getTag(ClockifyTask clockifyTask, Group group) {
        return Optional.ofNullable(clockifyTask.getTagIds())
                .flatMap(tagIds -> tagIds.stream()
                        .map(tagId -> tagService.getTagFromClockify(group, tagId))
                        .filter(tag -> Pattern.compile("^\\w+-\\d+$").matcher(tag.getName()).matches())
                        .findFirst())
                .orElseGet(Tag::new);
    }

    @Transactional
    public void deleteTask(Task task) {
        roleService.deleteAllRoles(task);
        taskRepository.delete(task);
    }

    @Transactional
    public void saveTask(ClockifyTask clockifyTask, Group group, User user) {
        Task task = findAllTasks().stream()
                .filter(t -> t.getTitleIssue().equals(clockifyTask.getDescription()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("The task with title " + clockifyTask.getDescription() + " does not exist."));
        task.setUser(user);
        saveTask(task);
        roleService.saveRole(getTag(clockifyTask, group).getName(), clockifyTask.getTimeInterval(), task);
    }
}
