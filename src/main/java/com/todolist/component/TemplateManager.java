package com.todolist.component;

import com.fadda.common.io.WriterManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TemplateManager {

    public ResponseEntity<String> getResponseEntity(WriterManager writerManager, String title) {
        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + title + "\"")
                .body(writerManager.get());
    }
    private static final String TEMPLATE_EXTENSION = ".txt";

    // Methods -----------------------------------------------------------------
    public WriterManager getTemplate(TemplateType templateType) throws IOException {
        String templatePath = TEMPLATES_DIRECTORY + templateType.getFileName() + TEMPLATE_EXTENSION;
        return new WriterManager(templatePath);
    }

    // Constants ---------------------------------------------------------------
    private static final String TEMPLATES_DIRECTORY = "templates/";
}






