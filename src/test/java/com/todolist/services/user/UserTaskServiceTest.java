package com.todolist.services.user;

import com.todolist.dtos.ShowTask;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.repositories.UserRepository;
import com.todolist.services.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserTaskServiceTest {

    @InjectMocks
    private UserTaskService userTaskService;

    @Mock
    private UserService userService;

    @Mock
    private TaskService taskService;

    @Mock
    private UserRepository userRepository;

    @Test
    void testGetShowTasksFromUser() {
        User user = new User();
        Task task1 = new Task();
        Task task2 = new Task();
        user.getTasks().add(task1);
        user.getTasks().add(task2);

        when(userService.findUserById(anyInt())).thenReturn(user);

        List<ShowTask> result = userTaskService.getShowTasksFromUser(user);

        assertEquals(2, result.size());
        verify(userService, times(1)).findUserById(anyInt());
    }

    @Test
    void testFindUsersWithTask() {
        Task task = new Task();
        task.setId(123);
        User user1 = new User();
        User user2 = new User();
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        when(userRepository.findAllByTaskId(task.getId())).thenReturn(users);

        List<User> result = userTaskService.findUsersWithTask(task);

        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAllByTaskId(task.getId());
    }

    // Add more test methods for other service methods
}

