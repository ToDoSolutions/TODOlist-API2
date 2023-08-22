package com.todolist.services;

import com.todolist.dtos.autodoc.clockify.ClockifyTask;
import com.todolist.entity.Group;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHIssue;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private RoleService roleService;

    @Mock
    private TagService tagService;

    @Mock
    private TaskRepository taskRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task(1, "Task 1"));
        tasks.add(new Task(2, "Task 2"));

        when(taskRepository.findAll(any(Sort.class))).thenReturn(tasks);

        List<Task> result = taskService.findAllTasks(Sort.by(Sort.Direction.ASC, "id"));

        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getId(), 1);
        assertEquals(result.get(1).getId(), 2);

        verify(taskRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void testFindTaskById() {
        Task task = new Task(1, "Task 1");

        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        Task result = taskService.findTaskById(1);

        assertEquals(result.getId(), 1);
        assertEquals(result.getTitle(), "Task 1");

        verify(taskRepository, times(1)).findById(1);
    }

    @Test
    void testSaveTask() {
        Task task = new Task(1, "Task 1");

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task result = taskService.saveTask(task);

        assertEquals(result, task);

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testDeleteTask() {
        Task task = new Task(1, "Task 1");

        taskService.deleteTask(task);

        verify(roleService, times(1)).deleteAllRoles(task);
        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    void testSaveTaskWithIssueAndClockifyTask() {
        GHIssue issue = new GHIssue("Issue Title", "Issue Body");
        ClockifyTask clockifyTask = new ClockifyTask("Task Description", "tagId", "interval");
        Group group = new Group();
        User user = new User();

        when(taskRepository.findAll()).thenReturn(new ArrayList<>());
        when(tagService.getTagFromClockify(group, "tagId")).thenReturn(new Tag("Tag Name"));

        taskService.saveTask(issue, clockifyTask, group, user);

        verify(taskRepository, times(1)).findAll();
        verify(tagService, times(1)).getTagFromClockify(group, "tagId");
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(roleService, times(1)).saveRole(eq("Tag Name"), eq("interval"), any(Task.class));
    }

    // Add more test methods for other service methods
}

