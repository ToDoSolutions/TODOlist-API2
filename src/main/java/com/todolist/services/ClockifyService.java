package com.todolist.services;

import com.todolist.component.FetchApiData;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.entity.autodoc.Role;
import com.todolist.entity.autodoc.clockify.ClockifyTask;
import com.todolist.entity.autodoc.clockify.Tag;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class ClockifyService {

    public static final String WORKSPACE_ID = "{workspaceId}";
    public static final String CLOCKIFY_ID = "{clockifyId}";
    public static final String TAG_ID = "{tagId}";
    public static final String X_API_KEY = "X-Api-Key";
    private final FetchApiData fetchApiData;
    private final UserService userService;
    @Value("${clockify.api.url.entries}")
    private String entriesUrl;
    @Value("${clockify.api.url.tags}")
    private String tagsUrl;
    @Value("${clockify.api.token}")
    private String token;

    @Autowired
    public ClockifyService(FetchApiData fetchApiData, UserService userService) {
        this.fetchApiData = fetchApiData;
        this.userService = userService;
    }

    // Get all task from a workspace
    public ClockifyTask[] getTaskFromWorkspace(String repoName, String username) {

        Task task = userService.findTaskByTitle(username, repoName);
        return userService.findUsersWithTask(task)
                .stream()
                .map(user -> fetchApiData.getApiDataWithToken(entriesUrl.replace(WORKSPACE_ID, task.getWorkSpaceId()).replace(CLOCKIFY_ID, user.getClockifyId()), ClockifyTask[].class, new Pair<>(X_API_KEY, token)))
                .flatMap(Stream::of).toArray(ClockifyTask[]::new);
    }

    public Tag getTagFromClockify(String repoName, String username, String tagId) {
        Task task = userService.findTaskByTitle(username, repoName);
        if (tagId == null)
            return new Tag();
        return fetchApiData.getApiDataWithToken(tagsUrl.replace(WORKSPACE_ID, task.getWorkSpaceId()).replace(TAG_ID, tagId), Tag.class, new Pair<>(X_API_KEY, token));
    }

    public Role getRoleFromClockify(String repoName, String username, String roleId) {
        return Role.parseTag(getTagFromClockify(repoName, username, roleId));
    }
}
