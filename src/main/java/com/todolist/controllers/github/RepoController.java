package com.todolist.controllers.github;

import com.google.common.base.Preconditions;
import com.todolist.dtos.ShowTask;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.entity.github.Repo;
import com.todolist.entity.github.TaskGitHub;
import com.todolist.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/github")
@Validated
@AllArgsConstructor
public class RepoController {

    private UserService userService;

    @GetMapping("repos/{idUser}")
    public List<Map<String, Object>> getAllRepos(
            @PathVariable long idUser,
            @RequestParam(defaultValue = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration") String fields) {
        User user = userService.findUserById(idUser);
        Preconditions.checkNotNull(user, "User not found.");
        String uri = "https://api.github.com/users/" + user.getUsername() + "/repos";
        RestTemplate restTemplate = new RestTemplate();
        TaskGitHub[] repos;
        if (user.getToken() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + user.getToken());
            repos = restTemplate.getForObject(uri, TaskGitHub[].class, headers);
        } else {
            repos = restTemplate.getForObject(uri, TaskGitHub[].class);
        }
        return Arrays.stream(repos).map(repo -> new ShowTask(Task.of(repo.getName(), repo.getDescription(), null, null, null, repo.getCreatedAt().split("T")[0], null, null)).getFields(fields)
        ).toList();
    }

    @GetMapping("user/{idUser}/repo/{repoName}")
    public Map<String, Object> getRepo(
            @PathVariable long idUser,
            @PathVariable String repoName,
            @RequestParam(defaultValue = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration") String fields) {
        User user = userService.findUserById(idUser);
        Preconditions.checkNotNull(user, "User not found.");
        String uri = "https://api.github.com/users/" + user.getUsername() + "/repos/" + repoName;
        RestTemplate restTemplate = new RestTemplate();
        TaskGitHub repo;
        if (user.getToken() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + user.getToken());
            repo = restTemplate.getForObject(uri, TaskGitHub.class, headers);
        } else {
            repo = restTemplate.getForObject(uri, TaskGitHub.class);
        }
        Task task = Task.of(repo.getName(), repo.getDescription(), null, null, null, repo.getCreatedAt().split("T")[0], null, null);
        return new ShowTask(task).getFields(fields);
    }

    @PostMapping("user/{idUser}/repo")
    public Repo addRepo(
            @RequestBody @Valid Repo createRepo,
            @PathVariable long idUser,
            @RequestParam(defaultValue = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration") String fields) {
        User user = userService.findUserById(idUser);
        Preconditions.checkNotNull(user, "User not found.");
        String uri = "https://api.github.com/user/repos";
        RestTemplate restTemplate = new RestTemplate();
        Preconditions.checkArgument(user.getToken() != null, "User must have a token.");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + user.getToken());
        restTemplate.postForObject(uri, createRepo, TaskGitHub.class, headers);
        return createRepo;
    }

    @PutMapping("user/{idUser}/repo/{repoName}")
    public Repo updateRepo(
            @RequestBody @Valid Repo updateRepo,
            @PathVariable long idUser,
            @PathVariable String repoName,
            @RequestParam(defaultValue = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration") String fields) {
        User user = userService.findUserById(idUser);
        Preconditions.checkNotNull(user, "User not found.");
        String uri = "https://api.github.com/repos" + user.getUsername() + "/" + repoName;
        RestTemplate restTemplate = new RestTemplate();
        Preconditions.checkArgument(user.getToken() != null, "User must have a token.");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + user.getToken());
        restTemplate.put(uri, updateRepo, TaskGitHub.class, headers);
        return updateRepo;
    }

    @DeleteMapping("user/{idUser}/repo/{repoName}")
    public void deleteRepo(
            @PathVariable long idUser,
            @PathVariable String repoName) {
        User user = userService.findUserById(idUser);
        Preconditions.checkNotNull(user, "User not found.");
        String uri = "https://api.github.com/repos" + user.getUsername() + "/" + repoName;
        RestTemplate restTemplate = new RestTemplate();
        Preconditions.checkArgument(user.getToken() != null, "User must have a token.");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + user.getToken());
        restTemplate.delete(uri, headers);
    }
}
