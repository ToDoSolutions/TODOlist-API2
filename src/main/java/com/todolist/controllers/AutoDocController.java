package com.todolist.controllers;

import com.todolist.entity.autodoc.TimeTask;
import com.todolist.services.AutoDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
    @RequestMapping("/{repoName}/{username}/md")
    public ResponseEntity<List<TimeTask>> getAutoDocMd(@PathVariable String repoName, @PathVariable String username) {
        return ResponseEntity.ok(autoDocService.autoDoc(repoName, username));
    }

    // TODO: Crear un endpoint para obtener el pdf
    @RequestMapping("/{repoName}/{username}/pdf")
    public ResponseEntity<List<TimeTask>> getAutoDocPdf(@PathVariable String repoName, @PathVariable String username) {
        return ResponseEntity.ok(autoDocService.autoDoc(repoName, username));
    }
}
