package com.todolist.services;

import com.todolist.component.FetchApiData;
import com.todolist.dtos.autodoc.RoleStatus;
import com.todolist.dtos.autodoc.clockify.ClockifyTask;
import com.todolist.entity.Group;
import com.todolist.entity.Tag;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class ClockifyService {

    // Constants ---------------------------------------------------------------
    public static final String WORKSPACE_ID = "{workspaceId}";
    public static final String CLOCKIFY_ID = "{clockifyId}";
    public static final String X_API_KEY = "X-Api-Key";
    // Services ---------------------------------------------------------------
    private final GroupService groupService;
    private final TagService tagService;
    // Components -------------------------------------------------------------
    private final FetchApiData fetchApiData;
    @Value("${clockify.api.token}")
    private String token;
    // Urls ------------------------------------------------------------------
    @Value("${clockify.api.url.entries}")
    private String entriesUrl;


    // Constructors -----------------------------------------------------------
    @Autowired
    public ClockifyService(FetchApiData fetchApiData, GroupService groupService, TagService tagService) {
        this.groupService = groupService;
        this.tagService = tagService;
        this.fetchApiData = fetchApiData;
    }

    // Methods ----------------------------------------------------------------
    public ClockifyTask[] getTaskFromWorkspace(String repoName, String username) { // Get all task from a workspace
        Group task = groupService.findTaskByTitle(username, repoName);
        return groupService.getUsersFromGroup(task)
                .stream()
                .map(user -> fetchApiData.getApiDataWithToken(entriesUrl.replace(WORKSPACE_ID, task.getWorkSpaceId()).replace(CLOCKIFY_ID, user.getClockifyId()), ClockifyTask[].class, new Pair<>(X_API_KEY, token)))
                .flatMap(Stream::of).toArray(ClockifyTask[]::new);
    }

    public Tag getTagFromClockify(String repoName, String username, String tagId) {
        Group task = groupService.findTaskByTitle(username, repoName);
        if (tagId == null)
            return new Tag();
        return tagService.getTagById(task, tagId);
    }

    public RoleStatus getRoleFromClockify(String repoName, String username, String roleId) {
        return RoleStatus.parseTag(getTagFromClockify(repoName, username, roleId));
    }
}
