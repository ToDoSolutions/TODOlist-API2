package com.todolist.controllers.group;

import com.todolist.entity.Group;
import com.todolist.entity.Task;
import com.todolist.services.TaskService;
import com.todolist.services.group.GroupService;
import com.todolist.services.group.GroupTaskService;
import com.todolist.services.group.GroupUserService;
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

@WebMvcTest(GroupTaskController.class)
class GroupTaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupService groupService;

    @MockBean
    private GroupTaskService groupTaskService;

    @MockBean
    private GroupUserService groupUserService;

    @MockBean
    private TaskService taskService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetGroupsWithTask() throws Exception {
        // Configure mocks
        when(taskService.findTaskById(anyInt())).thenReturn(new Task());
        when(groupTaskService.findGroupsWithTask(any())).thenReturn(Collections.emptyList());

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/groups/task/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify interactions
        verify(taskService, times(1)).findTaskById(anyInt());
        verify(groupTaskService, times(1)).findGroupsWithTask(any());
    }

    @Test
    void testAddTaskToGroup() throws Exception {
        // Configure mocks
        Group group = new Group();
        when(groupService.findGroupById(anyInt())).thenReturn(group);
        when(taskService.findTaskById(anyInt())).thenReturn(new Task());

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/group/1/task/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify interactions
        verify(groupService, times(1)).findGroupById(anyInt());
        verify(taskService, times(1)).findTaskById(anyInt());
        verify(groupTaskService, times(1)).addTaskToGroup(any(), any());
    }

    @Test
    void testDeleteAllTasksFromGroup() throws Exception {
        // Configure mocks
        Group group = new Group();
        when(groupService.findGroupById(anyInt())).thenReturn(group);

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/group/1/tasks"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify interactions
        verify(groupService, times(1)).findGroupById(anyInt());
        verify(groupTaskService, times(1)).removeAllTasksFromGroup(any());
    }

    @Test
    void testDeleteTaskFromGroup() throws Exception {
        // Configure mocks
        Group group = new Group();
        when(groupService.findGroupById(anyInt())).thenReturn(group);
        when(taskService.findTaskById(anyInt())).thenReturn(new Task());

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/group/1/task/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify interactions
        verify(groupService, times(1)).findGroupById(anyInt());
        verify(taskService, times(1)).findTaskById(anyInt());
        verify(groupTaskService, times(1)).removeTaskFromGroup(any(), any());
    }
}

