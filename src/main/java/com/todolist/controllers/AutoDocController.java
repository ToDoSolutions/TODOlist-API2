package com.todolist.controllers;

import com.todolist.entity.autodoc.Role;
import com.todolist.entity.autodoc.TimeTask;
import com.todolist.services.AutoDocService;
import net.steppschuh.markdowngenerator.table.Table;
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
    @RequestMapping("/{repoName}/{username}/md")
    public ResponseEntity getAutoDocMd(@PathVariable String repoName, @PathVariable String username) throws IOException {
        List<TimeTask> timeTasks = autoDocService.autoDoc(repoName, username);
        Table.Builder tableBuilder = new Table.Builder()
                .withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                .addRow("Título", "Descripción", "Responsables", "Rol", "Tiempo planificado", "Tiempo real", "Coste");
        for (TimeTask timeTask : timeTasks) {
            tableBuilder.addRow(timeTask.getTitle(), timeTask.getDescription(), timeTask.getUsernames().stream().reduce((s, s2) -> s + ", " + s2).orElse("")
                    , timeTask.getRoles().stream().map(Role::toString).reduce((s, s2) -> s + ", " + s2).orElse(""), "x"
                    , timeTask.getDuration()
                    , timeTask.getCost() + "€");
        }
        FileWriter fileWriter = new FileWriter("out/table.md");
        fileWriter.write(tableBuilder.build().serialize());
        fileWriter.close();
        return ResponseEntity.ok().build();
    }

    // TODO: Crear un endpoint para obtener el pdf
    @RequestMapping("/{repoName}/{username}/pdf")
    public ResponseEntity<List<TimeTask>> getAutoDocPdf(@PathVariable String repoName, @PathVariable String username) {
        return ResponseEntity.ok(autoDocService.autoDoc(repoName, username));
    }
}
