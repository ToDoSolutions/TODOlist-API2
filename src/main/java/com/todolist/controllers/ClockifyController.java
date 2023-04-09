package com.todolist.controllers;

import com.todolist.dtos.autodoc.clockify.ClockifyTask;
import com.todolist.services.ClockifyService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/v1/clockify")
@AllArgsConstructor
public class ClockifyController {

    // Services ---------------------------------------------------------------
    private final ClockifyService clockifyService;

    // Methods ----------------------------------------------------------------
    // Get all task from a workspace
    @GetMapping("/{repoName}/{username}/time-entries")
    public ResponseEntity<List<ClockifyTask>> getTaskFromWorkspace(@PathVariable String repoName, @PathVariable String username) {
        return ResponseEntity.ok(clockifyService.getTaskFromWorkspace(repoName, username));
    }
}
