package com.todolist.controllers.github;

import com.todolist.dtos.ShowTask;
import com.todolist.entity.github.Repo;
import com.todolist.services.github.RepoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/github")
@Validated
public class RepoController {



    @Autowired
    private RepoService repoService;


    // Obtener repositorios de un usuario de GitHub (ya existentes)
    @GetMapping("repos/{idUser}")
    public List<ShowTask> getAllRepos(@PathVariable long idUser) {
        return Arrays.stream(repoService.findAllRepos(idUser)).map(repo -> repoService.turnTaskGitHubIntoShowTask(repo, null, null, null)).toList();
    }

    // Obtener un repositorio de un usuario de GitHub (ya existente)
    @GetMapping("user/{idUser}/repo/{repoName}")
    public ShowTask getRepo(
            @PathVariable long idUser,
            @PathVariable String repoName) {
        return repoService.turnTaskGitHubIntoShowTask(repoService.findRepoByName(idUser, repoName), null, null, null);
    }

    // Obtener información de GitHub.

    // Crear un repositorio de un usuario de GitHub (ya existente)
    @PostMapping("user/{idUser}/repo")
    public Repo addRepo(
            @RequestBody @Valid Repo createRepo,
            @PathVariable long idUser,
            @RequestParam String password) {
        return repoService.saveRepo(idUser, createRepo, password);
    }

    // Actualizar un repositorio de un usuario de GitHub (ya existente)
    @PutMapping("user/{idUser}/repo/{repoName}")
    public Repo updateRepo(
            @RequestBody @Valid Repo updateRepo,
            @PathVariable long idUser,
            @PathVariable String repoName,
            @RequestParam String password) {
        return repoService.updateRepo(idUser, repoName, updateRepo, password);
    }

    // Eliminar un repositorio de un usuario de GitHub (ya existente)
    @DeleteMapping("user/{idUser}/repo/{repoName}")
    public void deleteRepo(
            @PathVariable long idUser,
            @PathVariable String repoName,
            @RequestParam String password) {
        repoService.deleteRepo(idUser, repoName, password);
    }

    // Subir información a GitHub.
    @PostMapping("user/{idUser}/task/{repoName}")
    public ShowTask addTask(@PathVariable long idUser,
                            @PathVariable String repoName,
                            @RequestParam(required = false) @Max(value = 5, message = "The priority must be between 0 and 5.") Long priority,
                            @RequestParam(required = false) @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The finishedDate is invalid.") String finishedDate,
                            @RequestParam(required = false) String difficulty) {
        return repoService.saveTask(idUser, repoName, finishedDate, priority,  difficulty);
    }
}
