package com.todolist.services.github;

import com.google.common.base.Preconditions;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.Status;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.entity.github.Release;
import com.todolist.entity.github.Repo;
import com.todolist.entity.github.TaskGitHub;
import com.todolist.exceptions.BadRequestException;
import com.todolist.exceptions.NotFoundException;
import com.todolist.services.TaskService;
import com.todolist.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class RepoService {

    @Value("${github.api.url}")
    private String startUrl;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;


    // TODO: revisar si en el caso de que se utilice una autorización cuenta también los privados.
    public TaskGitHub findRepoByName(Long idUser, String repoName) {
        User user = userService.findUserById(idUser);
        if (user == null)
            throw new NotFoundException("User not found");
        String url = startUrl + "/users/" + user.getUsername() + "/repos/" + repoName;
        RestTemplate restTemplate = new RestTemplate();
        if (user.getToken() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + user.getToken());
            return restTemplate.getForObject(url, TaskGitHub.class, headers);
        } else {
            return restTemplate.getForObject(url, TaskGitHub.class);
        }
    }

    public TaskGitHub[] findAllRepos(Long idUser) {
        User user = userService.findUserById(idUser);
        if (user == null)
            throw new NotFoundException("User not found.");
        Preconditions.checkNotNull(user, "User not found.");
        String url = startUrl + "/users/" + user.getUsername() + "/repos";
        RestTemplate restTemplate = new RestTemplate();
        if (user.getToken() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + user.getToken());
            return restTemplate.getForObject(url, TaskGitHub[].class, headers);
        } else {
            return restTemplate.getForObject(url, TaskGitHub[].class);
        }
    }

    public ShowTask saveTask(Long idUser, String repoName, String finishedDate, Long priority, String difficulty) {
        User user = userService.findUserById(idUser);
        if (user == null)
            throw new NotFoundException("User not found");
        String url = startUrl + "/users/" + user.getUsername() + "/repos/" + repoName;
        RestTemplate restTemplate = new RestTemplate();
        TaskGitHub repo;
        if (user.getToken() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + user.getToken());
            repo = restTemplate.getForObject(url, TaskGitHub.class, headers);
        } else
            repo = restTemplate.getForObject(url, TaskGitHub.class);
        Task task = turnTaskGitHubIntoTask(repo, finishedDate, priority, difficulty);
        task = taskService.saveTask(task);
        return new ShowTask(task);
    }

    public Repo saveRepo(Long idUser, Repo repo, String password) {
        User user = userService.findUserById(idUser);
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
        restTemplate.postForObject(url, repo, TaskGitHub.class, headers);
        return repo;
    }

    public Repo updateRepo(Long idUser, String repoName, Repo repo, String password) {
        User user = userService.findUserById(idUser);
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
        restTemplate.put(url, repo, TaskGitHub.class, headers);
        return repo;
    }

    public void deleteRepo(Long idUser, String repoName, String password) {
        User user = userService.findUserById(idUser);
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
        restTemplate.delete(url, headers);
    }

    public ShowTask turnTaskGitHubIntoShowTask(TaskGitHub taskGitHub, String finishedDate, Long priority, String difficulty) {
        return new ShowTask(turnTaskGitHubIntoTask(taskGitHub, finishedDate, priority, difficulty));
    }

    public Task turnTaskGitHubIntoTask(TaskGitHub taskGitHub, String finishedDate, Long priority, String difficulty) {
        return Task.of(
                taskGitHub.getName(),
                taskGitHub.getDescription(),
                taskGitHub.getGitUrl(),
                getStatus(taskGitHub.getReleasesUrl()),
                finishedDate,
                taskGitHub.getCreatedAt().split("T")[0], priority, difficulty);
    }

    private String getStatus(String releaseUrl) {
        RestTemplate restTemplate = new RestTemplate();
        String url = releaseUrl + "/latest";
        Release release = restTemplate.getForObject(url, Release.class);
        if (release == null) return Status.CANCELLED.toString();
        else if (release.getDraft()) return Status.DRAFT.toString();
        else if (release.getPrerelease()) return Status.DONE.toString();
        else return Status.IN_REVISION.toString();
    }
}
