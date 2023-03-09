package com.todolist.services.github;

import com.todolist.component.FetchApiData;
import com.todolist.component.GitHubConverter;
import com.todolist.dtos.Difficulty;
import com.todolist.entity.Group;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.entity.autodoc.github.Repo;
import com.todolist.entity.autodoc.github.TaskGitHub;
import com.todolist.services.GroupService;
import com.todolist.services.TaskService;
import com.todolist.services.UserService;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class RepoService {

    public static final String USERNAME = "{username}";
    public static final String REPO_NAME = "{repoName}";
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    private final GroupService groupService;
    private final UserService userService;
    private final TaskService taskService;
    private final GitHubConverter gitHubConverter;
    private final FetchApiData fetchApiData;
    @Value("${github.api.url.repos.all}")
    private String allReposUrl;
    @Value("${github.api.url.repos.one}")
    private String oneRepoUrl;
    @Value("${github.api.url.repos.authenticated}")
    private String authenticatedRepoUrl;
    @Value("${github.api.url.repos.organization}")
    private String organizationRepoUrl;

    @Autowired
    public RepoService(GroupService groupService, UserService userService, TaskService taskService, GitHubConverter gitHubConverter, FetchApiData fetchApiData) {
        this.groupService = groupService;
        this.userService = userService;
        this.taskService = taskService;
        this.gitHubConverter = gitHubConverter;
        this.fetchApiData = fetchApiData;
    }

    public TaskGitHub findRepoByName(String username, String repoName) {
        User user = userService.findUserByUsername(username);
        String url = oneRepoUrl.replace(USERNAME, user.getUsername()).replace(REPO_NAME, repoName);
        if (user.getToken() == null || user.getToken().isEmpty())
            return fetchApiData.getApiData(url, TaskGitHub.class);
        else
            return fetchApiData.getApiDataWithToken(url, TaskGitHub.class, new Pair<>(AUTHORIZATION, BEARER + user.getToken()));

    }

    public TaskGitHub[] findAllRepos(String username) {
        User user = userService.findUserByUsername(username);
        String url = allReposUrl.replace(USERNAME, user.getUsername());
        return fetchApiData.getApiDataWithToken(url, TaskGitHub[].class, new Pair<>(AUTHORIZATION, BEARER + user.getToken()));
    }

    public Task saveTask(String username, String repoName, LocalDate finishedDate, Long priority, Difficulty difficulty) {
        User user = userService.findUserByUsername(username);
        String url = oneRepoUrl.replace(USERNAME, user.getUsername()).replace(REPO_NAME, repoName);
        TaskGitHub repo = fetchApiData.getApiDataWithToken(url, TaskGitHub.class, new Pair<>(AUTHORIZATION, BEARER + user.getToken()));
        Task task = gitHubConverter.turnTaskGitHubIntoTask(repo, finishedDate, priority, difficulty);
        return taskService.saveTask(task);
    }

    public Repo saveRepo(String username, Repo repo) {
        User user = userService.findUserByUsername(username);
        fetchApiData.postApiDataWithToken(authenticatedRepoUrl, TaskGitHub.class, new Pair<>(AUTHORIZATION, BEARER + user.getToken()), repo);
        return repo;
    }

    public Repo saveRepo(String username, Long IdTask, Boolean haveAutoInit, Boolean isPrivate, String gitIgnoreTemplate, Boolean isTemplate, String homepage) {
        User user = userService.findUserByUsername(username);
        Task task = taskService.findTaskById(IdTask);
        Repo repo = gitHubConverter.turnTaskIntoRepo(task, haveAutoInit, isPrivate, gitIgnoreTemplate, isTemplate, homepage);
        return fetchApiData.postApiDataWithToken(authenticatedRepoUrl, Repo.class, new Pair<>(AUTHORIZATION, BEARER + user.getToken()), repo);
    }

    public Repo updateRepo(String username, String repoName, Repo repo) {
        User user = userService.findUserByUsername(username);
        String url = oneRepoUrl.replace(USERNAME, user.getUsername()).replace(REPO_NAME, repoName);
        fetchApiData.putApiDataWithToken(url, Repo.class, new Pair<>(AUTHORIZATION, BEARER + user.getToken()), repo);
        return repo;
    }

    public void deleteRepo(String username, String repoName) {
        User user = userService.findUserByUsername(username);
        String url = oneRepoUrl.replace(USERNAME, user.getUsername()).replace(REPO_NAME, repoName);
        fetchApiData.deleteApiDataWithToken(url, TaskGitHub.class, new Pair<>(AUTHORIZATION, BEARER + user.getToken()), null);
    }

    public Task findOrganizationWithRepo(Long idGroup) {
        Group group = groupService.findGroupById(idGroup);
        String url = organizationRepoUrl.replace("groupName", group.getName());
        TaskGitHub repo = fetchApiData.getApiData(url, TaskGitHub.class);
        return gitHubConverter.turnTaskGitHubIntoTask(repo, null, null, null);
    }
}
