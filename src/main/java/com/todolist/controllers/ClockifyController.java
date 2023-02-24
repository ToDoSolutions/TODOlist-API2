package com.todolist.controllers;

import com.todolist.entity.autodoc.clockify.ClockifyTask;
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

    private final ClockifyService clockifyService;

    @Autowired
    public ClockifyController(ClockifyService clockifyService) {
        this.clockifyService = clockifyService;
    }

    // Get all task from an workspace
    @GetMapping("/{repoName}/{username}/time-entries")
    public ResponseEntity<ClockifyTask[]> getTaskFromWorkspace(@PathVariable String repoName, @PathVariable String username) {
        return ResponseEntity.ok(clockifyService.getTaskFromWorkspace(repoName));
    }


}
