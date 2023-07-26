package com.todolist.controllers.autodoc;

import com.fadda.common.io.WriterManager;
import com.todolist.component.PlanningTable;
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
public class PlanningController {

    private final PlanningTable planningTable;
    private final TemplateManager templateManager;

    @GetMapping("/planning/md")
    public ResponseEntity<String> getPlanningGroup(@ModelAttribute Request requestDto) throws IOException {
        String[] output = planningTable.createPlanningTable(requestDto);
        WriterManager writerManager = requestDto.isIndividual() ?
                getWriterManager(TemplateType.PLANNING_INDIVIDUAL, requestDto.getIndividual(), output) :
                getWriterManager(TemplateType.PLANNING_GROUP, "", output);
        String filename = requestDto.isIndividual() ?
                "Informe de planificación - " + formatFilename(requestDto.getIndividual()) + ".md" :
                "Informe de planificación - Grupal.md";
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
