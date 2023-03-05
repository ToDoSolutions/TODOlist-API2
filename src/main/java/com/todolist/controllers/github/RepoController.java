package com.todolist.controllers.github;

import com.todolist.component.DTOManager;
import com.todolist.component.GitHubConverter;
import com.todolist.dtos.Difficulty;
import com.todolist.dtos.ShowTask;
import com.todolist.entity.Task;
import com.todolist.entity.autodoc.github.Repo;
import com.todolist.services.UserService;
import com.todolist.services.github.RepoService;
import com.todolist.validators.user.GitHubUserAuthenticatedValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/github")
public class RepoController {


    private final RepoService repoService;
    private final UserService userService;
    private final GitHubUserAuthenticatedValidator gitHubUserAuthenticatedValidator;
    private final DTOManager dtoManager;
    private final GitHubConverter gitHubConverter;


    @Autowired
    public RepoController(RepoService repoService, UserService userService, GitHubUserAuthenticatedValidator gitHubUserAuthenticatedValidator, DTOManager dtoManager, GitHubConverter gitHubConverter) {
        this.repoService = repoService;
        this.userService = userService;
        this.gitHubUserAuthenticatedValidator = gitHubUserAuthenticatedValidator;
        this.dtoManager = dtoManager;
        this.gitHubConverter = gitHubConverter;
    }

    /* OBTENER INFORMACIÓN Y MODIFICAR BASE DE DATOS */

    // Obtener repositorios de un usuario de GitHub (ya existentes)
    @GetMapping("/repos/{username}") // GetAllTest
    public List<Map<String, Object>> getAllRepos(@PathVariable String username,
                                                 @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES_STRING) String fieldsTask) {
        return Arrays.stream(repoService.findAllRepos(username))
                .map(repo -> dtoManager.getShowTaskAsJsonWithOutTimes(gitHubConverter.turnTaskGitHubIntoTask(repo, LocalDate.now(), null, null), fieldsTask))
                .toList();
    }

    // Obtener un repositorio de un usuario de GitHub (ya existente)
    @GetMapping("/user/{username}/repo/{repoName}") // GetSoloTest
    public Map<String, Object> getRepo(
            @PathVariable String username,
            @PathVariable String repoName,
            @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES_STRING) String fieldsTask) {

        return dtoManager.getShowTaskAsJsonWithOutTimes(gitHubConverter.turnTaskGitHubIntoTask(repoService.findRepoByName(username, repoName), LocalDate.now(), null, null), fieldsTask);
    }

    // Subir información a GitHub.
    @PostMapping("/user/{username}/task/{repoName}") // PostTest
    public Map<String, Object> addTask(@PathVariable String username,
                                       @PathVariable String repoName,
                                       @RequestParam(required = false) @Max(value = 5, message = "The priority must be between 0 and 5.") Long priority,
                                       @RequestParam(required = false) @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The finishedDate is invalid.") LocalDate finishedDate,
                                       @RequestParam(required = false) Difficulty difficulty) {
        Task task = repoService.saveTask(username, repoName, finishedDate, priority, difficulty);
        return dtoManager.getShowTaskAsJson(task);
    }

    /* MODIFICAR REPOS DE GITHUB*/

    // Eliminar un repositorio de un usuario de GitHub (ya existente)
    @DeleteMapping("user/{username}/repo/{repoName}") // DeleteTest
    public void deleteRepo(
            @PathVariable String username,
            @PathVariable String repoName,
            @RequestParam String password, BindingResult bindingResult) {
        gitHubUserAuthenticatedValidator.validateGitHubUserAuthenticated(password, userService.findUserByUsername(username), bindingResult);
        repoService.deleteRepo(username, repoName);
    }

    // Crear un repositorio de un usuario de GitHub (ya existente)
    @PostMapping("/user/{username}/repo") // PostTest
    public Repo addRepo(
            @RequestBody @Valid Repo createRepo,
            @PathVariable String username,
            @RequestParam String password, BindingResult bindingResult) {
        gitHubUserAuthenticatedValidator.validateGitHubUserAuthenticated(password, userService.findUserByUsername(username), bindingResult);
        return repoService.saveRepo(username, createRepo);
    }

    // Crear un repositorio de un usuario de GitHub (ya existente)
    @PostMapping("/user/{username}/repo/{idTask}") // PostTest
    public Repo addRepoExists(
            @PathVariable String username,
            @PathVariable long idTask,
            @RequestParam String password,
            @RequestParam(defaultValue = "true") @Pattern(regexp = "^(?)(true|false)$") Boolean haveAutoInit,
            @RequestParam(defaultValue = "false") @Pattern(regexp = "^(?)(true|false)$") Boolean isPrivate,
            @RequestParam(required = false) String gitIgnoreTemplate,
            @RequestParam(defaultValue = "false") @Pattern(regexp = "^(?)(true|false)$") Boolean isTemplate,
            @RequestParam(required = false) String homepage, BindingResult bindingResult) {
        gitHubUserAuthenticatedValidator.validateGitHubUserAuthenticated(password, userService.findUserByUsername(username), bindingResult);
        return repoService.saveRepo(username, idTask, haveAutoInit, isPrivate, gitIgnoreTemplate, isTemplate, homepage);
    }


    // Actualizar un repositorio de un usuario de GitHub (ya existente)
    @PutMapping("user/{username}/repo/{repoName}") // PutTest
    public Repo updateRepo(
            @RequestBody @Valid Repo updateRepo,
            @PathVariable String username,
            @PathVariable String repoName,
            @RequestParam String password,
            BindingResult bindingResult) {
        gitHubUserAuthenticatedValidator.validateGitHubUserAuthenticated(password, userService.findUserByUsername(username), bindingResult);
        return repoService.updateRepo(username, repoName, updateRepo);
    }
}
