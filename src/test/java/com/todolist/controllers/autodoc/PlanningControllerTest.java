package com.todolist.controllers.autodoc;

import com.fadda.common.io.WriterManager;
import com.todolist.component.PlanningTable;
import com.todolist.component.TemplateManager;
import com.todolist.dtos.autodoc.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(PlanningController.class)
class PlanningControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlanningTable planningTable;

    @MockBean
    private TemplateManager templateManager;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPlanningGroup() throws Exception {
        // Configure mocks
        when(planningTable.createPlanningTable(any(Request.class))).thenReturn(new String[]{"Mocked Output"});
        when(templateManager.getResponseEntity(any(WriterManager.class), anyString())).thenReturn(MockMvcResultMatchers.ok().content("Mocked Content")); // TODO: Check this

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/autodoc/planning/md"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string("Mocked Content"));

        // Verify interactions
        verify(planningTable, times(1)).createPlanningTable(any(Request.class));
        verify(templateManager, times(1)).getResponseEntity(any(WriterManager.class), anyString());
    }
}

