import com.todolist.controllers.user.UserTaskController;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.services.TaskService;
import com.todolist.services.user.UserService;
import com.todolist.services.user.UserTaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static org.mockito.Mockito.*;

@WebMvcTest(UserTaskController.class)
class UserTaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserTaskService userTaskService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserWithTask() throws Exception {
        // Configure mocks
        when(taskService.findTaskById(anyInt())).thenReturn(new Task());
        when(userTaskService.findUsersWithTask(any())).thenReturn(Collections.singletonList(new User()));

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/task/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify interactions
        verify(taskService, times(1)).findTaskById(anyInt());
        verify(userTaskService, times(1)).findUsersWithTask(any());
    }

    @Test
    void testAddTaskToUser() throws Exception {
        // Configure mocks
        User user = new User();
        Task task = new Task();
        when(userService.findUserById(anyInt())).thenReturn(user);
        when(taskService.findTaskById(anyInt())).thenReturn(task);

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user/1/task/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify interactions
        verify(userService, times(1)).findUserById(anyInt());
        verify(taskService, times(1)).findTaskById(anyInt());
        verify(userTaskService, times(1)).addTaskToUser(any(), any());
    }

    @Test
    void testDeleteAllTasksFromUser() throws Exception {
        // Configure mocks
        User user = new User();
        when(userService.findUserById(anyInt())).thenReturn(user);

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/user/1/tasks"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify interactions
        verify(userService, times(1)).findUserById(anyInt());
        verify(userTaskService, times(1)).removeAllTasksFromUser(any());
    }

    @Test
    void testDeleteTaskFromUser() throws Exception {
        // Configure mocks
        User user = new User();
        Task task = new Task();
        when(userService.findUserById(anyInt())).thenReturn(user);
        when(taskService.findTaskById(anyInt())).thenReturn(task);

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/user/1/task/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify interactions
        verify(userService, times(1)).findUserById(anyInt());
        verify(taskService, times(1)).findTaskById(anyInt());
        verify(userTaskService, times(1)).removeTaskFromUser(any(), any());
    }
}

