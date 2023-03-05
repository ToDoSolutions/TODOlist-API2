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
    @Value("${clockify.api.url.entries}")
    private String entriesUrl;

    @Value("${clockify.api.url.tags}")
    private String tagsUrl;

    @Value("${clockify.api.token}")
    private String token;

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
    public ClockifyTask[] getTaskFromWorkspace(String repoName) {
        Task task = taskService.findTaskByTitle(repoName);
       return userService.findUsersWithTask(task)
                .stream()
                .map(user -> fetchApiData.getApiDataWithToken(entriesUrl.replace(WORKSPACE_ID, task.getWorkSpaceId()).replace(CLOCKIFY_ID, user.getClockifyId()), ClockifyTask[].class, new Pair(X_API_KEY, token)))
               .flatMap(Stream::of).toArray(ClockifyTask[]::new);
    }

    // Get task for a user in a workspace
    public ClockifyTask[] getTaskForAUserFromWorkspace(String repoName, String username) {
        Task task = taskService.findTaskByTitle(repoName);
        User user = userService.findUserByUsername(username);
        return fetchApiData.getApiDataWithToken(entriesUrl.replace(WORKSPACE_ID, task.getWorkSpaceId()).replace(CLOCKIFY_ID, user.getClockifyId()), ClockifyTask[].class, new Pair(X_API_KEY, token));
    }

    public Tag getTagFromClockify(String repoName, String tagId) {
        Task task = taskService.findTaskByTitle(repoName);
        if (tagId == null)
            return new Tag();
        return fetchApiData.getApiDataWithToken(tagsUrl.replace(WORKSPACE_ID, task.getWorkSpaceId()).replace(TAG_ID, tagId), Tag.class, new Pair(X_API_KEY, token));
    }

    public Role getRoleFromClockify(String repoName, String roleId) {
        return Role.parseTag(getTagFromClockify(repoName, roleId));
    }
}
