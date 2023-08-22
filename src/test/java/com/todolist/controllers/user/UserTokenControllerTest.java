package com.todolist.controllers.user;

import com.todolist.controllers.user.UserTokenController;
import com.todolist.entity.User;
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

import javax.validation.Validator;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UserTokenController.class)
class UserTokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserTaskService userTaskService;

    @MockBean
    private Validator validator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetTokenFromUser() throws Exception {
        // Configure mocks
        User user = new User();
        user.setToken("testToken");
        when(userService.findUserById(anyInt())).thenReturn(user);

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/1/token")
                        .header("Password", "password"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("testToken"));

        // Verify interactions
        verify(userService, times(1)).findUserById(anyInt());
    }

    @Test
    public void testUpdateToken() throws Exception {
        // Configure mocks
        User user = new User();
        when(userService.findUserById(anyInt())).thenReturn(user);
        when(userService.saveUser(any())).thenReturn(user);

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user/1/token")
                        .header("Authorization", "Bearer newToken"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.token").value("newToken"));

        // Verify interactions
        verify(userService, times(1)).findUserById(anyInt());
        verify(userService, times(1)).saveUser(any());
    }
}

