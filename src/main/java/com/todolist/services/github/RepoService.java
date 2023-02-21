package com.todolist.services.github;

import com.todolist.dtos.ShowTask;
import com.todolist.dtos.Status;
import com.todolist.entity.Group;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.entity.autodoc.github.Release;
import com.todolist.entity.autodoc.github.Repo;
import com.todolist.entity.autodoc.github.TaskGitHub;
import com.todolist.exceptions.BadRequestException;
import com.todolist.exceptions.NotFoundException;
import com.todolist.services.GroupService;
import com.todolist.services.TaskService;
import com.todolist.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class RepoService {

    @Value("${github.api.url}")
    private String startUrl;

    @Autowired
    private GroupService  groupService;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;


    // TODO: revisar si en el caso de que se utilice una autorización cuenta también los privados.
    public TaskGitHub findRepoByName(String username, String repoName) {
        User user = userService.findUserByUsername(username);
        if (user == null) {
            user = new User();
            user.setUsername(username);
        }
        String url = startUrl + "/repos/" + user.getUsername() + "/" + repoName;
        RestTemplate restTemplate = new RestTemplate();
        if (user.getToken() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + user.getToken());
            return restTemplate.getForObject(url, TaskGitHub.class, headers);
        } else {
            return restTemplate.getForObject(url, TaskGitHub.class);
        }
    }

    public TaskGitHub[] findAllRepos(String username) {
        User user = userService.findUserByUsername(username);
        if (user == null) {
            user = new User();
            user.setUsername(username);
        }
        String url = startUrl + "/users/" + user.getUsername() + "/repos";
        RestTemplate restTemplate = new RestTemplate();
        if (user.getToken() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + user.getToken());
            return restTemplate.getForObject(url, TaskGitHub[].class, headers);
        } else
            return restTemplate.getForObject(url, TaskGitHub[].class);
    }

    public ShowTask saveTask(String username, String repoName, String finishedDate, Long priority, String difficulty) {
        User user = userService.findUserByUsername(username);
        if (user == null)
            throw new NotFoundException("User not found");
        String url = startUrl + "/repos/" + user.getUsername() + "/" + repoName;
        RestTemplate restTemplate = new RestTemplate();
        TaskGitHub repo;
        if (user.getToken() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + user.getToken());
            repo  = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null, headers), TaskGitHub.class).getBody();
        } else
            repo = restTemplate.getForObject(url, TaskGitHub.class);
        Task task = turnTaskGitHubIntoTask(repo, finishedDate, priority, difficulty);
        return new ShowTask(taskService.saveTask(task));
    }

    public Repo saveRepo(String username, Repo repo, String password) {
        User user = userService.findUserByUsername(username);
        if (user == null)
            throw new NotFoundException("User not found.");
        if (user.getToken() != null)
            throw new BadRequestException("User must have a token.");
        if (Objects.equals(password, user.getPassword()))
            throw new BadRequestException("Password is incorrect.");
        String url = startUrl + "/user/repos";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + user.getToken());
        restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(repo, headers), TaskGitHub.class);
        return repo;
    }

    public Repo saveRepo(String username, Long IdTask, String password, Boolean haveAutoInit, Boolean isPrivate, String gitIgnoreTemplate, Boolean isTemplate, String homepage) {
        User user = userService.findUserByUsername(username);
        Task task = taskService.findTaskById(IdTask);
        if (user == null)
            throw new NotFoundException("User not found.");
        if (user.getToken() != null)
            throw new BadRequestException("User must have a token.");
        if (task == null)
            throw new NotFoundException("Task not found.");
        if (Objects.equals(password, user.getPassword()))
            throw new BadRequestException("Password is incorrect.");
        String url = startUrl + "/user/repos";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + user.getToken());
        Repo repo = turnTaskIntoRepo(task, haveAutoInit, isPrivate, gitIgnoreTemplate, isTemplate, homepage);
        return restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(repo, headers), Repo.class).getBody();
        // Boolean haveAutoInit, Boolean isPrivate, String gitIgnoreTemplate, Boolean isTemplate, String homepage
    }

    public Repo updateRepo(String username, String repoName, Repo repo, String password) {
        User user = userService.findUserByUsername(username);
        if (user == null)
            throw new NotFoundException("User not found.");
        if (user.getToken() != null)
            throw new BadRequestException("User must have a token.");
        if (Objects.equals(password, user.getPassword()))
            throw new BadRequestException("Password is incorrect.");
        String url = startUrl + "/repos" + user.getUsername() + "/" + repoName;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + user.getToken());
        restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(null, headers), TaskGitHub.class);
        return repo;
    }

    public void deleteRepo(String username, String repoName, String password) {
        User user = userService.findUserByUsername(username);
        if (user == null)
            throw new NotFoundException("User not found.");
        if (user.getToken() != null)
            throw new BadRequestException("User must have a token.");
        if (Objects.equals(password, user.getPassword()))
            throw new BadRequestException("Password is incorrect.");
        String url = startUrl + "/repos" + user.getUsername() + "/" + repoName;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + user.getToken());
        restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(null, headers), TaskGitHub.class);
    }

    public ShowTask findOrganizationWithRepo(Long idGroup) {
        Group group = groupService.findGroupById(idGroup);
        if (group == null)
            throw new NotFoundException("Group not found");
        String url = startUrl + "/orgs/" + group.getName() + "/repos";
        RestTemplate restTemplate = new RestTemplate();
        TaskGitHub repo = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null), TaskGitHub.class).getBody();
        return turnTaskGitHubIntoShowTask(repo, null, null, null);
    }

    public ShowTask turnTaskGitHubIntoShowTask(TaskGitHub taskGitHub, String finishedDate, Long priority, String difficulty) {
        return new ShowTask(turnTaskGitHubIntoTask(taskGitHub, finishedDate, priority, difficulty));
    }

    public Task turnTaskGitHubIntoTask(TaskGitHub taskGitHub, String finishedDate, Long priority, String difficulty) {
        String status;
        try {
            status = getStatus(taskGitHub.getReleasesUrl());
        } catch (Exception e) {
            status = Status.UNKNOWN.toString();
        }
        return Task.of(
                taskGitHub.getName(),
                taskGitHub.getDescription(),
                taskGitHub.getCloneUrl(),
                status,
                finishedDate,
                taskGitHub.getCreatedAt().split("T")[0], priority, difficulty);
    }

    private String getStatus(String releaseUrl) {
        RestTemplate restTemplate = new RestTemplate();
        String url = releaseUrl + "/latest"; // Solo funciona si se pasa el token.
        Release release = restTemplate.getForObject(url, Release.class);
        if (release == null) return Status.CANCELLED.toString();
        else if (Boolean.TRUE.equals(release.getDraft())) return Status.DRAFT.toString();
        else if (Boolean.TRUE.equals(release.getPrerelease())) return Status.DONE.toString();
        else return Status.IN_REVISION.toString();
    }

    private Repo turnTaskIntoRepo(
            Task task, Boolean haveAutoInit, Boolean isPrivate, String gitIgnoreTemplate, Boolean isTemplate, String homepage) {
        Repo repo = new Repo();
        repo.setName(task.getTitle());
        repo.setDescription(task.getDescription());
        repo.setAutoInit(haveAutoInit);
        repo.setPrivate(isPrivate);
        repo.setGitignoreTemplate(gitIgnoreTemplate); // Poner
        repo.setTemplate(isTemplate);
        repo.setHomepage(homepage);
        return repo;
    }
}
