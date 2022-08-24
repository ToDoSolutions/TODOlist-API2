package com.todolist.controllers.github;

import com.google.common.base.Preconditions;
import com.todolist.dtos.ShowTask;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.entity.github.Repo;
import com.todolist.entity.github.TaskGitHub;
import com.todolist.exceptions.NotFoundException;
import com.todolist.services.TaskService;
import com.todolist.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/github")
@Validated
public class RepoController {

    @Value("${github.api.url}")
    private String startUrl;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    // Completado si contiene algún dato https://api.github.com/repos/alesanfe/PI1_kotlin/releases


    // Obtener repositorios de un usuario de GitHub (ya existentes)
    @GetMapping("repos/{idUser}")
    public List<Map<String, Object>> getAllRepos(
            @PathVariable long idUser,
            @RequestParam(defaultValue = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration") String fields) {
        User user = userService.findUserById(idUser);
        Preconditions.checkNotNull(user, "User not found.");
        String url = startUrl + "/users/" + user.getUsername() + "/repos";
        RestTemplate restTemplate = new RestTemplate();
        TaskGitHub[] repos;
        if (user.getToken() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + user.getToken());
            repos = restTemplate.getForObject(url, TaskGitHub[].class, headers);
        } else {
            repos = restTemplate.getForObject(url, TaskGitHub[].class);
        }
        return Arrays.stream(repos).map(repo -> new ShowTask(Task.of(repo.getName(), repo.getDescription(), null, null, null, repo.getCreatedAt().split("T")[0], null, null)).getFields(fields)
        ).toList();
    }

    // Obtener un repositorio de un usuario de GitHub (ya existente)
    @GetMapping("user/{idUser}/repo/{repoName}")
    public Map<String, Object> getRepo(
            @PathVariable long idUser,
            @PathVariable String repoName,
            @RequestParam(defaultValue = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration") String fields) {
        User user = userService.findUserById(idUser);
        Preconditions.checkNotNull(user, "User not found.");
        String url = startUrl + "/users/" + user.getUsername() + "/repos/" + repoName;
        RestTemplate restTemplate = new RestTemplate();
        TaskGitHub repo;
        if (user.getToken() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + user.getToken());
            repo = restTemplate.getForObject(url, TaskGitHub.class, headers);
        } else {
            repo = restTemplate.getForObject(url, TaskGitHub.class);
        }
        Task task = Task.of(repo.getName(), repo.getDescription(), null, null, null, repo.getCreatedAt().split("T")[0], null, null);
        return new ShowTask(task).getFields(fields);
    }

    // Obtener infromación de GitHub.

    // Crear un repositorio de un usuario de GitHub (ya existente)
    @PostMapping("user/{idUser}/repo")
    public Repo addRepo(
            @RequestBody @Valid Repo createRepo,
            @PathVariable long idUser,
            @RequestParam(defaultValue = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration") String fields) {
        User user = userService.findUserById(idUser);
        Preconditions.checkNotNull(user, "User not found.");
        String url = startUrl + "/user/repos";
        RestTemplate restTemplate = new RestTemplate();
        Preconditions.checkArgument(user.getToken() != null, "User must have a token.");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + user.getToken());
        restTemplate.postForObject(url, createRepo, TaskGitHub.class, headers);
        return createRepo;
    }

    // Actualizar un repositorio de un usuario de GitHub (ya existente)
    @PutMapping("user/{idUser}/repo/{repoName}")
    public Repo updateRepo(
            @RequestBody @Valid Repo updateRepo,
            @PathVariable long idUser,
            @PathVariable String repoName,
            @RequestParam(defaultValue = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration") String fields) {
        User user = userService.findUserById(idUser);
        Preconditions.checkNotNull(user, "User not found.");
        String url = startUrl + "/repos" + user.getUsername() + "/" + repoName;
        RestTemplate restTemplate = new RestTemplate();
        Preconditions.checkArgument(user.getToken() != null, "User must have a token.");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + user.getToken());
        restTemplate.put(url, updateRepo, TaskGitHub.class, headers);
        return updateRepo;
    }

    // Eliminar un repositorio de un usuario de GitHub (ya existente)
    @DeleteMapping("user/{idUser}/repo/{repoName}")
    public void deleteRepo(
            @PathVariable long idUser,
            @PathVariable String repoName) {
        User user = userService.findUserById(idUser);
        Preconditions.checkNotNull(user, "User not found.");
        String url = startUrl + "/repos" + user.getUsername() + "/" + repoName;
        RestTemplate restTemplate = new RestTemplate();
        Preconditions.checkArgument(user.getToken() != null, "User must have a token.");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + user.getToken());
        restTemplate.delete(url, headers);
    }

    // Subir información a GitHub.
    @PostMapping("user/{idUser}/task/{repoName}")
    public Map<String, Object> addTask(@PathVariable long idUser,
                                       @PathVariable String repoName,
                                       @RequestParam(required = false) @Pattern(regexp = "DRAFT|IN_PROGRESS|DONE|IN_REVISION|CANCELLED", message = "The status is invalid.") String status,
                                       @RequestParam(required = false) @Max(value = 5, message = "The priority must be between 0 and 5.") Long priority,
                                       @RequestParam(required = false) @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The finishedDate is invalid.") String finishedDate,
                                       @RequestParam(required = false) String annotation,
                                       @RequestParam(required = false) String difficulty) {
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
        Task task = Task.of(repo.getName(), repo.getDescription(), annotation, status, finishedDate, repo.getCreatedAt().split("T")[0], priority, difficulty);
        task = taskService.saveTask(task);
        return new ShowTask(task).getFields(ShowTask.ALL_ATTRIBUTES);
    }
}
