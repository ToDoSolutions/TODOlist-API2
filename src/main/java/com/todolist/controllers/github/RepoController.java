package com.todolist.controllers.github;

import com.todolist.dtos.ShowTask;
import com.todolist.entity.github.Repo;
import com.todolist.services.github.RepoService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/github")
@Validated
@AllArgsConstructor
public class RepoController {


    private RepoService repoService;

    /* OBTENER INFORMACIÓN Y MODIFICAR BASE DE DATOS */

    // Obtener repositorios de un usuario de GitHub (ya existentes)
    @GetMapping("/repos/{idUser}") // GetAllTest
    public List<Map<String, Object>> getAllRepos(@PathVariable long idUser,
                                                 @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES) String fieldsTask) {
        return Arrays.stream(repoService.findAllRepos(idUser)).map(repo -> repoService.turnTaskGitHubIntoShowTask(repo, null, null, null).getFields(fieldsTask)).toList();
    }

    // Obtener un repositorio de un usuario de GitHub (ya existente)
    @GetMapping("/user/{idUser}/repo/{repoName}") // GetSoloTest
    public Map<String, Object> getRepo(
            @PathVariable long idUser,
            @PathVariable String repoName,
            @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES) String fieldsTask) {
        return repoService.turnTaskGitHubIntoShowTask(repoService.findRepoByName(idUser, repoName), null, null, null).getFields(fieldsTask);
    }

    // Subir información a GitHub.
    @PostMapping("/user/{idUser}/task/{repoName}") // PostTest
    public Map<String, Object> addTask(@PathVariable long idUser,
                                       @PathVariable String repoName,
                                       @RequestParam(required = false) @Max(value = 5, message = "The priority must be between 0 and 5.") Long priority,
                                       @RequestParam(required = false) @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The finishedDate is invalid.") String finishedDate,
                                       @RequestParam(required = false) String difficulty) {
        return repoService.saveTask(idUser, repoName, finishedDate, priority, difficulty).getFields(ShowTask.ALL_ATTRIBUTES);
    }

    /* MODIFICAR REPOS DE GITHUB*/

    // Eliminar un repositorio de un usuario de GitHub (ya existente)
    @DeleteMapping("user/{idUser}/repo/{repoName}") // DeleteTest
    public void deleteRepo(
            @PathVariable long idUser,
            @PathVariable String repoName,
            @RequestParam String password) {
        repoService.deleteRepo(idUser, repoName, password);
    }

    // Crear un repositorio de un usuario de GitHub (ya existente)
    @PostMapping("/ser/{idUser}/repo") // PostTest
    public Repo addRepo(
            @RequestBody @Valid Repo createRepo,
            @PathVariable long idUser,
            @RequestParam String password) {
        return repoService.saveRepo(idUser, createRepo, password);
    }

    // Crear un repositorio de un usuario de GitHub (ya existente)
    @PostMapping("/ser/{idUser}/repo/{idTask}") // PostTest
    public Repo addRepoExists(
            @PathVariable long idUser,
            @PathVariable long idTask,
            @RequestParam String password,
            @RequestParam(defaultValue = "true") @Pattern(regexp ="^(?)(true|false)$") Boolean haveAutoInit,
            @RequestParam(defaultValue = "false") @Pattern(regexp ="^(?)(true|false)$") Boolean isPrivate,
            @RequestParam(required = false) String gitIgnoreTemplate,
            @RequestParam(defaultValue = "false") @Pattern(regexp ="^(?)(true|false)$") Boolean isTemplate,
            @RequestParam(required = false) String homepage) {
        return repoService.saveRepo(idUser, idTask, password, haveAutoInit, isPrivate, gitIgnoreTemplate, isTemplate, homepage);
        // Boolean haveAutoInit, Boolean isPrivate, String gitIgnoreTemplate, Boolean isTemplate, String homepage
    }



    // Actualizar un repositorio de un usuario de GitHub (ya existente)
    @PutMapping("user/{idUser}/repo/{repoName}") // PutTest
    public Repo updateRepo(
            @RequestBody @Valid Repo updateRepo,
            @PathVariable long idUser,
            @PathVariable String repoName,
            @RequestParam String password) {
        return repoService.updateRepo(idUser, repoName, updateRepo, password);
    }
}
