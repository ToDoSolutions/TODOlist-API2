package com.todolist.controllers;

import com.todolist.component.DTOManager;
import com.todolist.entity.Task;
import com.todolist.services.TaskService;
import com.todolist.services.group.GroupService;
import com.todolist.services.user.UserService;
import com.todolist.validators.FieldValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.Collections;
import static org.mockito.Mockito.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private UserService userService;

    @MockBean
    private GroupService groupService;

    @MockBean
    private DTOManager dtoManager;

    @Mock
    private FieldValidator fieldValidator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTasks() throws Exception {
        // Configure mocks
        when(taskService.findAllTasks(any())).thenReturn(Collections.emptyList());

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tasks"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify interactions
        verify(taskService, times(1)).findAllTasks(any());
    }

    @Test
    void testGetTask() throws Exception {
        // Configure mocks
        when(taskService.findTaskById(anyInt())).thenReturn(new Task());

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/task/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify interactions
        verify(taskService, times(1)).findTaskById(anyInt());
    }

    @Test
    void testAddTask() throws Exception {
        // Configure mocks
        Task task = new Task();
        when(taskService.saveTask(any())).thenReturn(task);

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/task")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Task Title\",\"description\":\"Task Description\"}")
                .param("idUser", "1")
                .param("idGroup", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify interactions
        verify(taskService, times(1)).saveTask(any());
        verify(userService, times(1)).findUserById(anyInt());
        verify(groupService, times(1)).findGroupById(anyInt());
    }

    @Test
    void testUpdateTask() throws Exception {
        // Configure mocks
        Task task = new Task();
        when(taskService.findTaskById(anyInt())).thenReturn(task);
        when(taskService.saveTask(any())).thenReturn(task);

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/task")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":1,\"title\":\"Updated Title\"}")
                .param("idUser", "1")
                .param("idGroup", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify interactions
        verify(taskService, times(1)).findTaskById(anyInt());
        verify(taskService, times(1)).saveTask(any());
        verify(userService, times(1)).findUserById(anyInt());
        verify(groupService, times(1)).findGroupById(anyInt());
    }

    @Test
    void testDeleteTask() throws Exception {
        // Configure mocks
        Task task = new Task();
        when(taskService.findTaskById(anyInt())).thenReturn(task);

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/task/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify interactions
        verify(taskService, times(1)).findTaskById(anyInt());
        verify(taskService, times(1)).deleteTask(any());
    }
}

