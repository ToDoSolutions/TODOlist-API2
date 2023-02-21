package com.todolist.services;

import com.todolist.component.FetchApiData;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.entity.autodoc.clockify.ClockifyTask;
import com.todolist.exceptions.NotFoundException;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ClockifyService {

    @Value("${clockify.api.url}")
    private String startUrl;

    private String token = "OGViNzFhMjMtOWVkZS00NWU2LWE2ZjUtYmU4ZmM1MThkYzUy";

    private final FetchApiData fetchApiData;
    private final TaskService taskService;
    private final UserService userService;

    @Autowired
    public ClockifyService(FetchApiData fetchApiData, TaskService taskService, UserService userService) {
        this.fetchApiData = fetchApiData;
        this.taskService = taskService;
        this.userService = userService;
    }

    // Get all task from an workspace
    public ClockifyTask[] getTaskFromWorkspace(String repoName, String username) {
        Task task = taskService.findTaskByTitle(repoName);
        if (task == null)
            throw new NotFoundException("Task not found");
        User user = userService.findUserByUsername(username);
        if (user == null)
            throw new NotFoundException("User not found");
        return fetchApiData.getApiDataWithToken(startUrl + "/workspaces/" + task.getWorkSpaceId() + "/user/" + user.getClockifyId() + "/time-entries", ClockifyTask[].class, new Pair("X-Api-Key", token));
    }
}
