package com.todolist.controllers;

import com.todolist.dtos.autodoc.clockify.ClockifyTask;
import com.todolist.services.ClockifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/clockify")
public class ClockifyController {

    // Services ---------------------------------------------------------------
    private final ClockifyService clockifyService;

    // Constructors -----------------------------------------------------------
    @Autowired
    public ClockifyController(ClockifyService clockifyService) {
        this.clockifyService = clockifyService;
    }

    // Methods ----------------------------------------------------------------
    // Get all task from a workspace
    @GetMapping("/{repoName}/{username}/time-entries")
    public ResponseEntity<ClockifyTask[]> getTaskFromWorkspace(@PathVariable String repoName, @PathVariable String username) {
        return ResponseEntity.ok(clockifyService.getTaskFromWorkspace(repoName, username));
    }
}
