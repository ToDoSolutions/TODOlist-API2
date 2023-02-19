package com.todolist.services;

import com.todolist.component.FetchApiData;
import com.todolist.entity.clockify.ClockifyTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ClockifyService {

    @Value("${clockify.api.url}")
    private String startUrl;

    private final FetchApiData fetchApiData;

    @Autowired
    public ClockifyService(FetchApiData fetchApiData) {
        this.fetchApiData = fetchApiData;
    }

    // Get all task from an workspace
    public ClockifyTask[] getTaskFromWorkspace(String workspaceId, String clockifyId) {
        return fetchApiData.getApiData(startUrl + "/workspaces/" + workspaceId + "/" + clockifyId + "/time-entries", ClockifyTask[].class);
    }
}
