package com.todolist.controllers.autodoc;

import com.fadda.common.io.WriterManager;
import com.todolist.component.PlanningTable;
import com.todolist.component.TemplateManager;
import com.todolist.component.TemplateType;
import com.todolist.dtos.autodoc.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/api/v1/autodoc")
public class PlanningController extends BaseAutodoc {

    // Services ---------------------------------------------------------------
    private final PlanningTable planningTable;

    // Constructors -----------------------------------------------------------
    @Autowired
    public PlanningController(PlanningTable planningTable, TemplateManager templateManager) {
        super(templateManager);
        this.planningTable = planningTable;
    }

    // Methods ----------------------------------------------------------------
    @GetMapping("/planning/md")
    public ResponseEntity<String> getPlanningGroup(@ModelAttribute Request requestDto) throws IOException {
        String[] output = planningTable.createPlanningTable(requestDto);
        WriterManager writerManager = Boolean.TRUE.equals(requestDto.isIndividual()) ?
                getWriterManager(TemplateType.PLANNING_INDIVIDUAL, requestDto.getIndividual(), output) :
                getWriterManager(TemplateType.PLANNING_GROUP, "", output);
        String filename = Boolean.TRUE.equals(requestDto.isIndividual()) ?
                "Informe de planificación - " + formatFilename(requestDto.getIndividual()) + ".md" :
                "Informe de planificación - Grupal.md";
        return templateManager.getResponseEntity(writerManager, filename);
    }
}
