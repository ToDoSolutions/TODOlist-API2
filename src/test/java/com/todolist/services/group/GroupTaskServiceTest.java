package com.todolist.services.group;

import com.todolist.entity.Group;
import com.todolist.entity.Task;
import com.todolist.repositories.GroupTaskRepository;
import com.todolist.services.TaskService;
import com.todolist.services.user.UserTaskService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class GroupTaskServiceTest {

    @InjectMocks
    private GroupTaskService groupTaskService;

    @Mock
    private GroupUserService groupUserService;

    @Mock
    private TaskService taskService;

    @Mock
    private UserTaskService userTaskService;

    @Mock
    private GroupTaskRepository groupTaskRepository;

    @Test
    void testFindGroupsWithTask() {
        Task task = new Task();
        task.setId(123);

        Group group1 = new Group();
        Group group2 = new Group();
        List<Group> groups = new ArrayList<>();
        groups.add(group1);
        groups.add(group2);

        when(groupTaskRepository.findAllGroupsByTaskId(task.getId())).thenReturn(groups);

        List<Group> result = groupTaskService.findGroupsWithTask(task);

        assertEquals(2, result.size());
        verify(groupTaskRepository, times(1)).findAllGroupsByTaskId(task.getId());
    }

    @Test
    void testGetTasksFromGroup() {
        Group group = new Group();
        group.setId(456);

        Task task1 = new Task();
        Task task2 = new Task();
        List<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);

        when(groupTaskRepository.findAllTasksFromGroupId(group.getId())).thenReturn(tasks);

        List<Task> result = groupTaskService.getTasksFromGroup(group);

        assertEquals(2, result.size());
        verify(groupTaskRepository, times(1)).findAllTasksFromGroupId(group.getId());
    }

    // Add more test methods for other service methods
}

