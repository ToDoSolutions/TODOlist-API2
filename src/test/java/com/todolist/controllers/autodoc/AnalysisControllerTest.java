package com.todolist.controllers.autodoc;

import com.fadda.common.io.WriterManager;
import com.todolist.component.AnalysisTable;
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

@WebMvcTest(AnalysisController.class)
class AnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalysisTable analysisTable;

    @MockBean
    private TemplateManager templateManager;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAnalysisGroup() throws Exception {
        // Configure mocks
        when(analysisTable.createAnalysisTable(any(Request.class))).thenReturn("Mocked Output");
        when(templateManager.getResponseEntity(any(WriterManager.class), anyString())).thenReturn(MockMvcResultMatchers.ok().content("Mocked Content")); // TODO: Check this

        // Perform request and assertions
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/autodoc/analysis/md"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string("Mocked Content"));

        // Verify interactions
        verify(analysisTable, times(1)).createAnalysisTable(any(Request.class));
        verify(templateManager, times(1)).getResponseEntity(any(WriterManager.class), anyString());
    }
}

