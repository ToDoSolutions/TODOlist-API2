package com.todolist.controllers.github;

import com.todolist.dtos.ShowTask;
import com.todolist.entity.autodoc.github.Repo;
import com.todolist.services.UserService;
import com.todolist.services.github.RepoService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/github")
@Validated
@AllArgsConstructor
public class RepoController {


    private RepoService repoService;
    private UserService userService;

    /* OBTENER INFORMACIÓN Y MODIFICAR BASE DE DATOS */

    // Obtener repositorios de un usuario de GitHub (ya existentes)
    @GetMapping("/repos/{username}") // GetAllTest
    public List<Map<String, Object>> getAllRepos(@PathVariable String username,
                                                 @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES) String fieldsTask) {
        return Arrays.stream(repoService.findAllRepos(username)).map(repo -> repoService.turnTaskGitHubIntoShowTask(repo, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE), null, null)
                .getFields(fieldsTask.replace("finishedDate", "").replace("priority", "").replace("difficulty", "").replace("duration", "").replace("idTask", ""))).toList();
    }

    // Obtener un repositorio de un usuario de GitHub (ya existente)
    @GetMapping("/user/{username}/repo/{repoName}") // GetSoloTest
    public Map<String, Object> getRepo(
            @PathVariable String username,
            @PathVariable String repoName,
            @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES) String fieldsTask) {
        return repoService.turnTaskGitHubIntoShowTask(repoService.findRepoByName(username, repoName), LocalDateTime.now().format(DateTimeFormatter.ISO_DATE), null, null)
                .getFields(fieldsTask.replace("finishedDate", "").replace("priority", "").replace("difficulty", "").replace("duration", "").replace("idTask",""));
    }

    // Subir información a GitHub.
    @PostMapping("/user/{username}/task/{repoName}") // PostTest
    public Map<String, Object> addTask(@PathVariable String username,
                                       @PathVariable String repoName,
                                       @RequestParam(required = false) @Max(value = 5, message = "The priority must be between 0 and 5.") Long priority,
                                       @RequestParam(required = false) @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The finishedDate is invalid.") String finishedDate,
                                       @RequestParam(required = false) String difficulty) {
        return repoService.saveTask(username, repoName, finishedDate, priority, difficulty).getFields(ShowTask.ALL_ATTRIBUTES);
    }

    /* MODIFICAR REPOS DE GITHUB*/

    // Eliminar un repositorio de un usuario de GitHub (ya existente)
    @DeleteMapping("user/{username}/repo/{repoName}") // DeleteTest
    public void deleteRepo(
            @PathVariable String username,
            @PathVariable String repoName,
            @RequestParam String password) {
        repoService.deleteRepo(username, repoName, password);
    }

    // Crear un repositorio de un usuario de GitHub (ya existente)
    @PostMapping("/user/{idUser}/repo") // PostTest
    public Repo addRepo(
            @RequestBody @Valid Repo createRepo,
            @PathVariable String username,
            @RequestParam String password) {
        return repoService.saveRepo(username, createRepo, password);
    }

    // Crear un repositorio de un usuario de GitHub (ya existente)
    @PostMapping("/user/{username}/repo/{idTask}") // PostTest
    public Repo addRepoExists(
            @PathVariable String username,
            @PathVariable long idTask,
            @RequestParam String password,
            @RequestParam(defaultValue = "true") @Pattern(regexp ="^(?)(true|false)$") Boolean haveAutoInit,
            @RequestParam(defaultValue = "false") @Pattern(regexp ="^(?)(true|false)$") Boolean isPrivate,
            @RequestParam(required = false) String gitIgnoreTemplate,
            @RequestParam(defaultValue = "false") @Pattern(regexp ="^(?)(true|false)$") Boolean isTemplate,
            @RequestParam(required = false) String homepage) {
        return repoService.saveRepo(username, idTask, password, haveAutoInit, isPrivate, gitIgnoreTemplate, isTemplate, homepage);
        // Boolean haveAutoInit, Boolean isPrivate, String gitIgnoreTemplate, Boolean isTemplate, String homepage
    }



    // Actualizar un repositorio de un usuario de GitHub (ya existente)
    @PutMapping("user/{username}/repo/{repoName}") // PutTest
    public Repo updateRepo(
            @RequestBody @Valid Repo updateRepo,
            @PathVariable String username,
            @PathVariable String repoName,
            @RequestParam String password) {
        return repoService.updateRepo(username, repoName, updateRepo, password);
    }
}
