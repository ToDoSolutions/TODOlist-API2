package com.todolist.services.github;

import com.todolist.component.FetchApiData;
import com.todolist.dtos.autodoc.github.TaskGitHub;
import com.todolist.entity.User;
import com.todolist.services.GroupService;
import com.todolist.services.UserService;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RepoService {

    // Constants --------------------------------------------------------------
    public static final String USERNAME = "{username}";
    public static final String REPO_NAME = "{repoName}";
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";

    // Services ---------------------------------------------------------------
    private final UserService userService;
    private final FetchApiData fetchApiData;

    // Urls -------------------------------------------------------------------
    @Value("${github.api.url.repos.one}")
    private String oneRepoUrl;

    // Constructors -----------------------------------------------------------
    @Autowired
    public RepoService(GroupService groupService, UserService userService, FetchApiData fetchApiData) {
        this.userService = userService;
        this.fetchApiData = fetchApiData;
    }

    // Methods ----------------------------------------------------------------
    public TaskGitHub findRepoByName(String username, String repoName) {
        User user = userService.findUserByUsername(username);
        String url = oneRepoUrl.replace(USERNAME, user.getUsername()).replace(REPO_NAME, repoName);
        if (user.getToken() == null || user.getToken().isEmpty())
            return fetchApiData.getApiData(url, TaskGitHub.class);
        else
            return fetchApiData.getApiDataWithToken(url, TaskGitHub.class, new Pair<>(AUTHORIZATION, BEARER + user.getToken()));

    }
}
