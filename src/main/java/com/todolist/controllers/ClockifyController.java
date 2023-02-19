package com.todolist.controllers;

import com.todolist.entity.clockify.ClockifyTask;
import com.todolist.services.ClockifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
    @GetMapping("/workspaces/{workspaceId}/{clockifyId}/time-entries")
    public ClockifyTask[] getTaskFromWorkspace(String workspaceId, String clockifyId) {
        return clockifyService.getTaskFromWorkspace(workspaceId, clockifyId);
    }

    @GetMapping("/workspaces/{workspaceId}/{clockifyId}/time-entries/md")
    public ClockifyTask[] getTaskFromWorkspaceWithMarkdown(String workspaceId, String clockifyId) {
        // Devolver un markdown con los datos de las tareas.
        return clockifyService.getTaskFromWorkspace(workspaceId, clockifyId);
    }


}
