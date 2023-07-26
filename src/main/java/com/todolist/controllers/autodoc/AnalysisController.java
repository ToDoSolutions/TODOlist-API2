package com.todolist.controllers.autodoc;

import com.fadda.common.io.WriterManager;
import com.todolist.component.AnalysisTable;
import com.todolist.component.TemplateManager;
import com.todolist.component.TemplateType;
import com.todolist.dtos.autodoc.Request;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.time.LocalDate;

@Controller
@RequestMapping("/api/v1/autodoc")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisTable analysisTable;
    private final TemplateManager templateManager;

    @GetMapping("/analysis/md")
    public ResponseEntity<String> getAnalysisGroup(@ModelAttribute Request requestDto) throws IOException {
        String output = analysisTable.createAnalysisTable(requestDto);
        WriterManager writerManager = Boolean.TRUE.equals(requestDto.isIndividual()) ?
                getWriterManager(TemplateType.ANALYSIS_INDIVIDUAL, output, requestDto.getIndividual()) :
                getWriterManager(TemplateType.ANALYSIS_GROUP, output);
        String filename = Boolean.TRUE.equals(requestDto.isIndividual()) ?
                "Informe de análisis - " + formatFilename(requestDto.getIndividual()) + ".md" :
                "Informe de análisis - Grupal.md";
        return templateManager.getResponseEntity(writerManager, filename);
    }

    private WriterManager getWriterManager(TemplateType templateType, Object content, String... placeholders) throws IOException {
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

    private String formatFilename(String filename) {
        return filename.toLowerCase().replace(" ", "_");
    }
}
