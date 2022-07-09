package com.todolist.resources.github;

import com.todolist.dtos.ShowTask;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.entity.github.Repo;
import com.todolist.entity.github.TaskGitHub;
import com.todolist.repository.Repositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/github")
@Validated
public class RepoResource {

    @Autowired
    @Qualifier("repositories")
    private Repositories repositories;

    @GetMapping("repos/{idUser}")
    public List<Map<String, Object>> getAllRepos(
            @PathVariable long idUser,
            @RequestParam(defaultValue = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration") String fields) {
        User user = repositories.findUserById(idUser);
        if (user == null)
            throw new IllegalArgumentException("User not found");
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
        return Arrays.asList(repos).stream().map(repo -> {
            Task task = new Task();
            task.setTitle(repo.getName());
            task.setDescription(repo.getDescription());
            task.setStatus(null);
            task.setFinishedDate(null);
            task.setStartDate(repo.getCreatedAt().split("T")[0]);
            task.setPriority(null);
            task.setAnnotation(null);
            task.setDifficulty(null);
            task.setIdTask(-1);
            return new ShowTask(task).getFields(fields);
        }).collect(Collectors.toList());
    }

    @GetMapping("user/{idUser}/repo/{repoName}")
    public Map<String, Object> getRepo(
            @PathVariable long idUser,
            @PathVariable String repoName,
            @RequestParam(defaultValue = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration") String fields) {
        User user = repositories.findUserById(idUser);
        if (user == null)
            throw new IllegalArgumentException("User not found");
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
        Task task = new Task();
        task.setTitle(repo.getName());
        task.setDescription(repo.getDescription());
        task.setStatus(null);
        task.setFinishedDate(null);
        task.setStartDate(repo.getCreatedAt().split("T")[0]);
        task.setPriority(null);
        task.setAnnotation(null);
        task.setDifficulty(null);
        task.setIdTask(-1);
        return new ShowTask(task).getFields(fields);
    }

    @PostMapping("user/{idUser}/repo")
    public Repo addRepo(
            @RequestBody @Valid Repo createRepo,
            @PathVariable long idUser,
            @RequestParam(defaultValue = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration") String fields) {
        if (createRepo.getName() == null)
            throw new IllegalArgumentException("Name is required");
        User user = repositories.findUserById(idUser);
        if (user == null)
            throw new IllegalArgumentException("User not found");
        String uri = "https://api.github.com/user/repos";
        RestTemplate restTemplate = new RestTemplate();
        if (user.getToken() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + user.getToken());
            restTemplate.postForObject(uri, createRepo, TaskGitHub.class, headers);
        } else {
            throw new IllegalArgumentException("The user has not a token.");
        }
        return createRepo;
    }

    @PutMapping("user/{idUser}/repo/{repoName}")
    public Repo updateRepo(
            @RequestBody @Valid Repo updateRepo,
            @PathVariable long idUser,
            @PathVariable String repoName,
            @RequestParam(defaultValue = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration") String fields) {
        if (updateRepo.getName() == null)
            throw new IllegalArgumentException("Name is required");
        User user = repositories.findUserById(idUser);
        if (user == null)
            throw new IllegalArgumentException("User not found");
        String uri = "https://api.github.com/repos" + user.getUsername() + "/" + repoName;
        RestTemplate restTemplate = new RestTemplate();
        TaskGitHub repo;
        if (user.getToken() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + user.getToken());
            restTemplate.put(uri, updateRepo, TaskGitHub.class, headers);
        } else {
            throw new IllegalArgumentException("The user has not a token.");
        }
        return updateRepo;
    }

    @DeleteMapping("user/{idUser}/repo/{repoName}")
    public void deleteRepo(
            @PathVariable long idUser,
            @PathVariable String repoName) {
        User user = repositories.findUserById(idUser);
        if (user == null)
            throw new IllegalArgumentException("User not found");
        String uri = "https://api.github.com/repos" + user.getUsername() + "/" + repoName;
        RestTemplate restTemplate = new RestTemplate();
        if (user.getToken() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + user.getToken());
            restTemplate.delete(uri, headers);
        } else {
            throw new IllegalArgumentException("The user has not a token.");
        }
    }
}
