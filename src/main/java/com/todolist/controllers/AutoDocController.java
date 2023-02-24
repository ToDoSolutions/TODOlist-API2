package com.todolist.controllers;


import com.todolist.entity.autodoc.TimeTask;
import com.todolist.services.AutoDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.*;
import java.util.List;

@Controller
@RequestMapping("/api/v1/autodoc")
// TODO: Debe permitir obtener el de todo el grupo o solo uno.
public class AutoDocController {

    private final AutoDocService autoDocService;

    @Autowired
    public AutoDocController(AutoDocService autoDocService) {
        this.autoDocService = autoDocService;
    }

    @RequestMapping("/{repoName}/{username}")
    public ResponseEntity<List<TimeTask>> getAutoDoc(@PathVariable String repoName, @PathVariable String username) {
        return ResponseEntity.ok(autoDocService.autoDoc(repoName, username));
    }

    // TODO: Crear un endpoint para obtener el markdown
    @RequestMapping("/planning/{repoName}/{username}/md")
    public ResponseEntity getPlanning(@PathVariable String repoName, @PathVariable String username, @RequestParam(defaultValue = "all") String individual) throws IOException {
        String[] output = autoDocService.getPlanning(repoName, username, individual);
        File file = new File("templates/planning.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String text =reader.lines().reduce((s, s2) -> s + s2 + "\n").get();
        reader.close();
        String newContent = text.replace("{content0}", output[0]);
        newContent = newContent.replace("{content1}", output[1]);
        newContent = newContent.replace("{content2}", output[2]);
        FileWriter fileWriter = new FileWriter("out/planning.md");
        fileWriter.write(newContent);
        fileWriter.close();
        return ResponseEntity.ok().build();
    }

    @RequestMapping("/analysis/{repoName}/{username}/md")
    public ResponseEntity getAnalysis(@PathVariable String repoName, @PathVariable String username, @RequestParam(defaultValue = "all") String individual) throws IOException {
        String output = autoDocService.getAnalysis(repoName, username, individual);
        File file = new File("templates/analysis.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String text =reader.lines().reduce((s, s2) -> s + s2 + "\n").get();
        reader.close();
        String newContent = text.replace("{content}", output);
        FileWriter fileWriter = new FileWriter("out/analysis.md");
        fileWriter.write(newContent);
        fileWriter.close();
        return ResponseEntity.ok().build();
    }


}
