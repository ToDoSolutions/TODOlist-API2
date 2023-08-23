package com.todolist.controllers.group;

import com.todolist.component.DTOManager;
import com.todolist.entity.Group;
import com.todolist.services.group.GroupService;
import com.todolist.services.group.GroupUserService;
import com.todolist.validators.FieldValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.Collections;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(GroupController.class)
@ComponentScan(basePackages = "com.todolist.converters")
class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupService groupService;

    @MockBean
    private GroupUserService groupUserService;

    @MockBean
    private DTOManager dtoManager;

    @MockBean
    private FieldValidator fieldValidator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllGroups() throws Exception {
        // Configure mocks
        when(groupService.findAllGroups(any())).thenReturn(Collections.emptyList());

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/groups"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify interactions
        verify(groupService, times(1)).findAllGroups(any());
    }

    @Test
    void testGetGroup() throws Exception {
        // Configure mocks
        when(groupService.findGroupById(anyInt())).thenReturn(new Group());

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/group/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify interactions
        verify(groupService, times(1)).findGroupById(anyInt());
    }

    @Test
    void testAddGroup() throws Exception {
        // Configure mocks
        Group group = new Group();
        when(groupService.saveGroup(any())).thenReturn(group);

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/group")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test Group\",\"description\":\"Test Description\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify interactions
        verify(groupService, times(1)).saveGroup(any());
    }

    @Test
    void testUpdateGroup() throws Exception {
        // Configure mocks
        Group group = new Group();
        when(groupService.findGroupById(anyInt())).thenReturn(group);
        when(groupService.saveGroup(any())).thenReturn(group);

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/group")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":1,\"name\":\"Updated Group\",\"description\":\"Updated Description\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify interactions
        verify(groupService, times(1)).findGroupById(anyInt());
        verify(groupService, times(1)).saveGroup(any());
    }

    @Test
    void testDeleteGroup() throws Exception {
        // Configure mocks
        Group group = new Group();
        when(groupService.findGroupById(anyInt())).thenReturn(group);

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/group/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify interactions
        verify(groupService, times(1)).findGroupById(anyInt());
        verify(groupService, times(1)).deleteGroup(any());
    }
}

