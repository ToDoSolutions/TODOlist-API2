package com.todolist.controllers.autodoc;

import com.fadda.common.io.WriterManager;
import com.todolist.component.AnalysisTable;
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
public class AnalysisController extends BaseAutodoc {


    // Services ---------------------------------------------------------------
    private final AnalysisTable analysisTable;

    // Constructors -----------------------------------------------------------
    @Autowired
    public AnalysisController(AnalysisTable analysisTable, TemplateManager templateManager) {
        super(templateManager);
        this.analysisTable = analysisTable;
    }

    // Methods ----------------------------------------------------------------
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
}
