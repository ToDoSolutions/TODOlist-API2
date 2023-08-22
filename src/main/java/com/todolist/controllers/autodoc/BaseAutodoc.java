package com.todolist.controllers.autodoc;

import com.fadda.common.io.WriterManager;
import com.todolist.component.TemplateManager;
import com.todolist.component.TemplateType;

import java.io.IOException;
import java.time.LocalDate;

public class BaseAutodoc {
    // Components --------------------------------------------------------------
    final TemplateManager templateManager;

    // Constructors ------------------------------------------------------------
    public BaseAutodoc(TemplateManager templateManager) {
        this.templateManager = templateManager;
    }

    // Auxiliary methods ------------------------------------------------------
    WriterManager getWriterManager(TemplateType templateType, String content, String... placeholders) throws IOException {
        return getTemplateWriterManager(templateType)
                .map(s -> replacePlaceholders(s, content, placeholders));
    }

    private WriterManager getTemplateWriterManager(TemplateType templateType) throws IOException {
        return templateManager.getTemplate(templateType)
                .map(s -> s.replace("{creationDate}", LocalDate.now().toString()));
    }

    private String replacePlaceholders(String template, Object content, String... placeholders) {
        String replacedTemplate = template.replace("{content}", content.toString());
        for (int i = 0; i < placeholders.length; i++) {
            replacedTemplate = replacedTemplate.replace("{content" + i + "}", placeholders[i]);
        }
        return replacedTemplate;
    }

    String formatFilename(String filename) {
        return filename.toLowerCase().replace(SPACE, LINE);
    }

    // Constants --------------------------------------------------------------
    public static final String SPACE = " ";
    public static final String LINE = "_";
}
