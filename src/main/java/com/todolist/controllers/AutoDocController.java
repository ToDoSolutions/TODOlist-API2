package com.todolist.controllers;

import com.google.common.collect.Lists;
import com.todolist.entity.autodoc.Employee;
import com.todolist.entity.autodoc.Role;
import com.todolist.entity.autodoc.TimeTask;
import com.todolist.services.AutoDocService;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        List<Employee> employees = autoDocService.getEmployees(timeTasks);
        Table.Builder times = new Table.Builder()
                .withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                .addRow("Título", "Descripción", "Responsables", "Rol", "Tiempo planificado", "Tiempo real", "Coste");
        // Obtenemos la tabla con los costes de cada tarea.
        for (TimeTask timeTask : timeTasks) {
            times.addRow(timeTask.getTitle(), timeTask.getDescription(), timeTask.getEmployees().stream().map(Employee::getName).reduce((s, s2) -> s + ", " + s2).orElse("")
                    , timeTask.getRoles().stream().map(Role::toString).reduce((s, s2) -> s + ", " + s2).orElse(""), "x"
                    , timeTask.getDuration()
                    , timeTask.getCost() + "€");
        }
        String output = times.build().serialize();
        // Obtenemos la tabla para los empleados.

        for (Employee employee : employees) {
            output += "\n" + new Heading(employee.getName(), 2) + "\n";
            output += new Table.Builder().withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                    .addRow("Rol", "Coste")
                    .addRow("Desarrollador", employee.getSalaryByRole(Role.DEVELOPER) + "€")
                    .addRow("Analista", employee.getSalaryByRole(Role.ANALYST) + "€")
                    .addRow("Tester", employee.getSalaryByRole(Role.TESTER) + "€")
                    .addRow("Diseñador", employee.getSalaryByRole(Role.MANAGER) + "€")
                    .addRow("Gerente", employee.getSalaryByRole(Role.OPERATOR) + "€")
                    .build().serialize();
            times.
                    addRow(employee.getName(), "", "", "", "", "", employee.getSalary().entrySet().stream().map(entry -> entry.getKey() + ": " + entry.getValue() + "€").reduce((s, s2) -> s + ", " + s2).orElse(""));
        }

        FileWriter fileWriter = new FileWriter("out/table.md");
        fileWriter.write(output);
        fileWriter.close();
        return ResponseEntity.ok().build();
    }

    // TODO: Crear un endpoint para obtener el pdf
    @RequestMapping("/{repoName}/{username}/pdf")
    public ResponseEntity<List<TimeTask>> getAutoDocPdf(@PathVariable String repoName, @PathVariable String username) {
        return ResponseEntity.ok(autoDocService.autoDoc(repoName, username));
    }


}
