package com.todolist.component;

import com.fadda.common.io.WriterManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TemplateManager {

    public static final String TEMPLATES_ANALYSIS_GROUP = "templates/analysis_group.txt";
    public static final String TEMPLATES_ANALYSIS_INDIVIDUAL = "templates/analysis_individual.txt";
    public static final String TEMPLATES_PLANNING_GROUP = "templates/planning_group.txt";
    public static final String TEMPLATES_PLANNING_INDIVIDUAL = "templates/planning_individual.txt";



    public WriterManager getGroupAnalysisTemplate() throws IOException {
        return new WriterManager(TEMPLATES_ANALYSIS_GROUP);
    }

    public WriterManager getIndividualAnalysisTemplate() throws IOException {
        return new WriterManager(TEMPLATES_ANALYSIS_INDIVIDUAL);
    }

    public WriterManager getGroupPlanningTemplate() throws IOException {
        return new WriterManager(TEMPLATES_PLANNING_GROUP);
    }

    public WriterManager getIndividualPlanningTemplate() throws IOException {
        return new WriterManager(TEMPLATES_PLANNING_INDIVIDUAL);
    }

    public ResponseEntity<String> getResponseEntity(WriterManager writerManager, String title) {
        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + title + "\"")
                .body(writerManager.get());
    }
}
