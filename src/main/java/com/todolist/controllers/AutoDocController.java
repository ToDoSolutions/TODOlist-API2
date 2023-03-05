package com.todolist.controllers;


import com.todolist.entity.autodoc.TimeTask;
import com.todolist.services.AutoDocService;
import com.todolist.utilities.WriterManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/api/v1/autodoc")
public class AutoDocController {

    public static final String ALL = "all";
    public static final String PLANNING_GROUP = "planning_group.md";
    public static final String PLANNING_INDIVIDUAL = "planning_{username}.md";
    public static final String USERNAME = "{username}";
    public static final String SPACE = " ";
    public static final String LINE = "_";
    public static final String ANALYSIS_GROUP = "analysis_group.md";
    public static final String ANALYSIS_INDIVIDUAL = "analysis_{username}.md";
    public static final String TEMPLATES_ANALYSIS = "src/main/resources/templates/analysis.txt";
    public static final String TEMPLATES_PLANNING = "src/main/resources/templates/planning.txt";
    private final AutoDocService autoDocService;

    @Autowired
    public AutoDocController(AutoDocService autoDocService) {
        this.autoDocService = autoDocService;
    }

    @RequestMapping("/{repoName}/{username}")
    public List<TimeTask> getAutoDoc(@PathVariable String repoName, @PathVariable String username) {
        return autoDocService.autoDoc(repoName, username);
    }

    @RequestMapping("/planning/{repoName}/{username}/md")
    public ResponseEntity<String> getPlanning(@PathVariable String repoName, @PathVariable String username, @RequestParam(defaultValue = ALL) String individual) throws IOException {
        String[] output = autoDocService.getPlanning(repoName, username, individual);
        String fileName= individual.equals(ALL) ? PLANNING_GROUP : PLANNING_INDIVIDUAL.replace(USERNAME, individual.toLowerCase().replace(SPACE, LINE));
        return new WriterManager(TEMPLATES_PLANNING)
                .map(s -> s.replace("{content0}", output[0]))
                .map(s -> s.replace("{content1}", output[1]))
                .map(s -> s.replace("{content2}", output[2]))
                .get(fileName);
    }

    @RequestMapping("/analysis/{repoName}/{username}/md")
    public ResponseEntity<String> getAnalysis(@PathVariable String repoName, @PathVariable String username, @RequestParam(defaultValue = ALL) String individual) throws IOException {
        String output = autoDocService.getAnalysis(repoName, username, individual);
        String fileName = individual.equals(ALL) ? ANALYSIS_GROUP : ANALYSIS_INDIVIDUAL.replace(USERNAME, individual.toLowerCase().replace(SPACE, LINE));
        return new WriterManager(TEMPLATES_ANALYSIS)
                .map(s -> s.replace("{content}", output))
                .get(fileName);
    }

}
