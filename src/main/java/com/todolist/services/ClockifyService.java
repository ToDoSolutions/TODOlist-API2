package com.todolist.services;

import com.todolist.component.FetchApiData;
import com.todolist.dtos.autodoc.clockify.ClockifyTask;
import com.todolist.entity.Group;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ClockifyService {

    // Constants ---------------------------------------------------------------
    public static final String WORKSPACE_ID = "{workspaceId}";
    public static final String CLOCKIFY_ID = "{clockifyId}";
    public static final String X_API_KEY = "X-Api-Key";
    // Services ---------------------------------------------------------------
    private final GroupService groupService;
    // Components -------------------------------------------------------------
    private final FetchApiData fetchApiData;
    @Value("${clockify.api.token}")
    private String token;
    // Urls ------------------------------------------------------------------
    @Value("${clockify.api.url.entries}")
    private String entriesUrl;


    // Constructors -----------------------------------------------------------
    @Autowired
    public ClockifyService(FetchApiData fetchApiData, GroupService groupService) {
        this.groupService = groupService;
        this.fetchApiData = fetchApiData;
    }

    // Methods ----------------------------------------------------------------
    public List<ClockifyTask> getTaskFromWorkspace(String repoName, String username) { // Get all task from a workspace
        Group task = groupService.findTaskByTitle(username, repoName);
        return groupService.getUsersFromGroup(task).stream()
                .flatMap(user -> Arrays.stream(fetchApiData.getApiDataWithToken(entriesUrl.replace(WORKSPACE_ID, task.getWorkSpaceId()).replace(CLOCKIFY_ID, user.getClockifyId()), ClockifyTask[].class, new Pair<>(X_API_KEY, token))))
                .toList();
    }
}
