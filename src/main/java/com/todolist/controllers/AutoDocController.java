package com.todolist.controllers;


import com.fadda.common.io.WriterManager;
import com.todolist.component.TemplateManager;
import com.todolist.services.AutoDocService;
import com.todolist.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;

@Controller
@RequestMapping("/api/v1/autodoc")
public class AutoDocController {

    // Constants --------------------------------------------------------------
    public static final String PLANNING_GROUP = "Informe de planificación - Grupal.md";
    public static final String PLANNING_INDIVIDUAL = "Informe de planificación - {username}.md";
    public static final String USERNAME = "{username}";
    public static final String SPACE = " ";
    public static final String LINE = "_";
    public static final String ANALYSIS_GROUP = "Informe de análisis - Grupal.md";
    public static final String ANALYSIS_INDIVIDUAL = "Informe de análisis - {username}.md";
    public static final String TEMPLATES_ANALYSIS_GROUP = "templates/analysis_group.txt";
    public static final String TEMPLATES_ANALYSIS_INDIVIDUAL = "templates/analysis_individual.txt";
    public static final String TEMPLATES_PLANNING_GROUP = "templates/planning_group.txt";
    public static final String TEMPLATES_PLANNING_INDIVIDUAL = "templates/planning_individual.txt";
    public static final String GROUP = "G";
    public static final String INDIVIDUAL = "I";

    // Services ---------------------------------------------------------------
    private final AutoDocService autoDocService;
    private final TaskService taskService;

    // Components -------------------------------------------------------------
    private final TemplateManager templateManager;

    // Constructors -----------------------------------------------------------
    @Autowired
    public AutoDocController(AutoDocService autoDocService, TaskService taskService, TemplateManager templateManager) {
        this.autoDocService = autoDocService;
        this.taskService = taskService;
        this.templateManager = templateManager;
    }

    // Methods ----------------------------------------------------------------
    @RequestMapping("/planning/{repoName}/{username}/md")
    public ResponseEntity<String> getPlanningGroup(@PathVariable String repoName, @PathVariable String username, @RequestParam(defaultValue = GROUP) String title) throws IOException {
        String[] output = autoDocService.getPlanning(repoName, username, title);
        WriterManager writerManager = templateManager.getGroupPlanningTemplate()
                .map(s -> s.replace("{creationDate}", LocalDate.now().toString()))
                .map(s -> s.replace("{content0}", output[0]))
                .map(s -> s.replace("{content1}", output[1]))
                .map(s -> s.replace("{content2}", output[2]));
        return templateManager.getResponseEntity(writerManager, PLANNING_GROUP);
    }

    @RequestMapping("/analysis/{repoName}/{username}/md")
    public ResponseEntity<String> getAnalysisGroup(@PathVariable String repoName, @PathVariable String username, @RequestParam(defaultValue = GROUP) String title) throws IOException {
        String output = autoDocService.getAnalysis(repoName, username, title);
        WriterManager writerManager = templateManager.getGroupAnalysisTemplate()
                .map(s -> s.replace("{creationDate}", LocalDate.now().toString()))
                .map(s -> s.replace("{content}", output));
        return templateManager.getResponseEntity(writerManager, ANALYSIS_GROUP);
    }

    @RequestMapping("/planning/{repoName}/{username}/individual/{individual}/md")
    public ResponseEntity<String> getPlanningIndividual(@PathVariable String repoName, @PathVariable String username, @PathVariable String individual, @RequestParam(defaultValue = INDIVIDUAL) String title) throws IOException {
        String[] output = autoDocService.getPlanning(repoName, username, individual, title);
        WriterManager writerManager = templateManager.getIndividualPlanningTemplate()
                .map(s -> s.replace("{creationDate}", LocalDate.now().toString()))
                .map(s -> s.replace("{content0}", output[0]))
                .map(s -> s.replace("{content1}", output[1]))
                .map(s -> s.replace("{content2}", output[2]))
                .map(s -> s.replace("{content3}", output[3]))
                .map(s -> s.replace("{roles}", output[4]))
                .map(s -> s.replace("{individual}", individual));
        return templateManager.getResponseEntity(writerManager, PLANNING_INDIVIDUAL.replace(USERNAME, individual.toLowerCase().replace(SPACE, LINE)));
    }

    @RequestMapping("/analysis/{repoName}/{username}/individual/{individual}/md")
    public ResponseEntity<String> getAnalysisIndividual(@PathVariable String repoName, @PathVariable String username, @PathVariable String individual, @RequestParam(defaultValue = INDIVIDUAL) String title) throws IOException {
        taskService.deleteAll();
        String output = autoDocService.getAnalysis(repoName, username, individual, title);
        WriterManager writerManager = templateManager.getIndividualAnalysisTemplate()
                .map(s -> s.replace("{creationDate}", LocalDate.now().toString()))
                .map(s -> s.replace("{content}", output))
                .map(s -> s.replace("{individual}", individual));
        return templateManager.getResponseEntity(writerManager, ANALYSIS_INDIVIDUAL.replace(USERNAME, individual.toLowerCase().replace(SPACE, LINE)));
    }

}
