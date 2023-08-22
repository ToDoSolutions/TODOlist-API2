package com.todolist.controllers.user;

import com.todolist.component.DTOManager;
import com.todolist.entity.User;
import com.todolist.services.user.UserService;
import com.todolist.services.user.UserTaskService;
import com.todolist.validators.FieldValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserTaskService userTaskService;

    @MockBean
    private FieldValidator fieldValidator;

    @MockBean
    private DTOManager dtoManager;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers() throws Exception {
        // Configure mocks
        when(userService.findAllUsers(any())).thenReturn(Collections.emptyList());

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify interactions
        verify(userService, times(1)).findAllUsers(any());
    }

    @Test
    void testGetUser() throws Exception {
        // Configure mocks
        when(userService.findUserById(anyInt())).thenReturn(new User());

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify interactions
        verify(userService, times(1)).findUserById(anyInt());
    }

    @Test
    void testAddUser() throws Exception {
        // Configure mocks
        User user = new User();
        when(userService.saveUser(any())).thenReturn(user);

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"John\",\"email\":\"john@example.com\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify interactions
        verify(userService, times(1)).saveUser(any());
    }

    @Test
    void testUpdateUser() throws Exception {
        // Configure mocks
        User user = new User();
        when(userService.findUserById(anyInt())).thenReturn(user);
        when(userService.saveUser(any())).thenReturn(user);

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":1,\"name\":\"Updated Name\"}")
                .header("Password", "password"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify interactions
        verify(userService, times(1)).findUserById(anyInt());
        verify(userService, times(1)).saveUser(any());
    }

    @Test
    void testDeleteUser() throws Exception {
        // Configure mocks
        User user = new User();
        when(userService.findUserById(anyInt())).thenReturn(user);

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/user/1")
                .header("Password", "password"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify interactions
        verify(userService, times(1)).findUserById(anyInt());
        verify(userService, times(1)).deleteUser(any());
    }
}

