package com.todolist.controllers.group;

import com.todolist.entity.Group;
import com.todolist.entity.User;
import com.todolist.services.group.GroupService;
import com.todolist.services.group.GroupUserService;
import com.todolist.services.user.UserService;
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
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(GroupUserController.class)
class GroupUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupService groupService;

    @MockBean
    private UserService userService;

    @MockBean
    private GroupUserService groupUserService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetGroupsWithUser() throws Exception {
        // Configure mocks
        when(userService.findUserById(anyInt())).thenReturn(new User());
        when(groupUserService.findGroupsWithUser(any())).thenReturn(Collections.emptyList());

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/groups/user/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify interactions
        verify(userService, times(1)).findUserById(anyInt());
        verify(groupUserService, times(1)).findGroupsWithUser(any());
    }

    @Test
    void testAddUserToGroup() throws Exception {
        // Configure mocks
        Group group = new Group();
        when(groupService.findGroupById(anyInt())).thenReturn(group);
        when(userService.findUserById(anyInt())).thenReturn(new User());

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/group/1/user/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify interactions
        verify(groupService, times(1)).findGroupById(anyInt());
        verify(userService, times(1)).findUserById(anyInt());
        verify(groupUserService, times(1)).addUserToGroup(any(), any());
    }

    @Test
    void testDeleteAllUsersFromGroup() throws Exception {
        // Configure mocks
        Group group = new Group();
        when(groupService.findGroupById(anyInt())).thenReturn(group);

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/group/1/users"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify interactions
        verify(groupService, times(1)).findGroupById(anyInt());
        verify(groupUserService, times(1)).removeAllUsersFromGroup(any());
    }

    @Test
    void testDeleteUserFromGroup() throws Exception {
        // Configure mocks
        Group group = new Group();
        when(groupService.findGroupById(anyInt())).thenReturn(group);
        when(userService.findUserById(anyInt())).thenReturn(new User());

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/group/1/user/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify interactions
        verify(groupService, times(1)).findGroupById(anyInt());
        verify(userService, times(1)).findUserById(anyInt());
        verify(groupUserService, times(1)).removeUserFromGroup(any(), any());
    }
}

