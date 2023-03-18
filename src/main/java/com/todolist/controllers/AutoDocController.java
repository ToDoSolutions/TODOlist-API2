package com.todolist.controllers;


import com.todolist.entity.autodoc.TimeTask;
import com.todolist.services.AutoDocService;
import com.todolist.utilities.WriterManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/api/v1/autodoc")
public class AutoDocController {

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
    private final AutoDocService autoDocService;

    @Autowired
    public AutoDocController(AutoDocService autoDocService) {
        this.autoDocService = autoDocService;
    }

    @RequestMapping("/{repoName}/{username}")
    public ResponseEntity<List<TimeTask>> getAutoDoc(@PathVariable String repoName, @PathVariable String username) {
        List<TimeTask> timeTasks = autoDocService.autoDoc(repoName, username);
        return ResponseEntity.ok(timeTasks);
    }

    @RequestMapping("/planning/{repoName}/{username}/md")
    public ResponseEntity<String> getPlanningGroup(@PathVariable String repoName, @PathVariable String username, @RequestParam(defaultValue = GROUP) String title) throws IOException {
        String[] output = autoDocService.getPlanning(repoName, username, title);
        return new WriterManager(TEMPLATES_PLANNING_GROUP)
                .map(s -> s.replace("{creationDate}", LocalDate.now().toString()))
                .map(s -> s.replace("{content0}", output[0]))
                .map(s -> s.replace("{content1}", output[1]))
                .map(s -> s.replace("{content2}", output[2]))
                .get(PLANNING_GROUP);
    }

    @RequestMapping("/analysis/{repoName}/{username}/md")
    public ResponseEntity<String> getAnalysisGroup(@PathVariable String repoName, @PathVariable String username, @RequestParam(defaultValue = GROUP) String title) throws IOException {
        String output = autoDocService.getAnalysis(repoName, username, title);
        return new WriterManager(TEMPLATES_ANALYSIS_GROUP)
                .map(s -> s.replace("{creationDate}", LocalDate.now().toString()))
                .map(s -> s.replace("{content}", output))
                .get(ANALYSIS_GROUP);
    }

    @RequestMapping("/planning/{repoName}/{username}/individual/{individual}/md")
    public ResponseEntity<String> getPlanningIndividual(@PathVariable String repoName, @PathVariable String username, @PathVariable String individual, @RequestParam(defaultValue = INDIVIDUAL) String title) throws IOException {
        String[] output = autoDocService.getPlanning(repoName, username, individual, title);
        return new WriterManager(TEMPLATES_PLANNING_INDIVIDUAL)
                .map(s -> s.replace("{creationDate}", LocalDate.now().toString()))
                .map(s -> s.replace("{content0}", output[0]))
                .map(s -> s.replace("{content1}", output[1]))
                .map(s -> s.replace("{content2}", output[2]))
                .map(s -> s.replace("{content3}", output[3]))
                .map(s -> s.replace("{roles}", output[4]))
                .map(s -> s.replace("{individual}", individual))

                .get(PLANNING_INDIVIDUAL.replace(USERNAME, individual.toLowerCase().replace(SPACE, LINE)));
    }

    @RequestMapping("/analysis/{repoName}/{username}/individual/{individual}/md")
    public ResponseEntity<String> getAnalysisIndividual(@PathVariable String repoName, @PathVariable String username, @PathVariable String individual, @RequestParam(defaultValue = INDIVIDUAL) String title) throws IOException {
        String output = autoDocService.getAnalysis(repoName, username, individual, title);
        return new WriterManager(TEMPLATES_ANALYSIS_INDIVIDUAL)
                .map(s -> s.replace("{creationDate}", LocalDate.now().toString()))
                .map(s -> s.replace("{content}", output))
                .map(s -> s.replace("{individual}", individual))
                .get(ANALYSIS_INDIVIDUAL.replace(USERNAME, individual.toLowerCase().replace(SPACE, LINE)));
    }

}
