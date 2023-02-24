package com.todolist.controllers;


import com.todolist.entity.autodoc.TimeTask;
import com.todolist.services.AutoDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileWriter;
import java.io.IOException;
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
    public ResponseEntity getPlanningMd(@PathVariable String repoName, @PathVariable String username) throws IOException {
        String output = autoDocService.getPlanning(repoName, username);
        FileWriter fileWriter = new FileWriter("out/planning.md");
        fileWriter.write(output.toString());
        fileWriter.close();
        return ResponseEntity.ok().build();
    }

    // TODO: Crear un endpoint para obtener el pdf
    @RequestMapping("/planning/{repoName}/{username}/pdf")
    public ResponseEntity getPlanningPdf(@PathVariable String repoName, @PathVariable String username) throws IOException {
        String output = autoDocService.getPlanning(repoName, username);
        System.out.println(output);
        FileWriter fileWriter = new FileWriter("out/planning.md");
        fileWriter.write(output.toString());
        fileWriter.close();

        //Converter converter = new Converter("out/table.md");
        //PdfConvertOptions options = new PdfConvertOptions();
        //converter.convert("out/table.pdf", options);


        return ResponseEntity.ok().build();
    }

    @RequestMapping("/analysis/{repoName}/{username}/md")
    public ResponseEntity getAnalysisMd(@PathVariable String repoName, @PathVariable String username) throws IOException {
        String output = autoDocService.getAnalysis(repoName, username);
        FileWriter fileWriter = new FileWriter("out/analysis.md");
        fileWriter.write(output.toString());
        fileWriter.close();
        return ResponseEntity.ok().build();
    }


}
