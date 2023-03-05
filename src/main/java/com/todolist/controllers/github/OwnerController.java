package com.todolist.controllers.github;

import com.todolist.component.DTOManager;
import com.todolist.component.GitHubConverter;
import com.todolist.entity.User;
import com.todolist.exceptions.BadRequestException;
import com.todolist.services.UserService;
import com.todolist.services.github.OwnerService;
import com.todolist.validators.user.GitHubUserAuthenticatedValidator;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Validated
public class OwnerController {

    public static final String EASY_PASSWORD = "1234";
    private final OwnerService ownerService;

    private final UserService userService;

    private final GitHubConverter gitHubConverter;
    private final DTOManager dtoManager;
    private final GitHubUserAuthenticatedValidator gitHubUserAuthenticatedValidator;

    public OwnerController(OwnerService ownerService, UserService userService, GitHubConverter gitHubConverter, DTOManager dtoManager, GitHubUserAuthenticatedValidator gitHubUserAuthenticatedValidator) {
        this.ownerService = ownerService;
        this.userService = userService;
        this.gitHubConverter = gitHubConverter;
        this.dtoManager = dtoManager;
        this.gitHubUserAuthenticatedValidator = gitHubUserAuthenticatedValidator;
    }



    /* OBTENER INFORMACIÓN Y MODIFICAR BASE DE DATOS */

    // Obtener usuario de GitHub (ya existente)
    @GetMapping("/github/user/{username}") // GetSoloTest
    public Map<String, Object> getOwner(@PathVariable String username) {
        return dtoManager.getShowUserAsJson(gitHubConverter.turnOwnerIntoUser(ownerService.findByUsername(username), EASY_PASSWORD));
    }

    @PutMapping("/github/user/{username}") // PutTest
    public Map<String, Object> updateUser(@PathVariable String username) {
        User oldUser = userService.findUserByUsername(username);
        return dtoManager.getShowUserAsJson(ownerService.updateUser(oldUser));
    }

    /* MODIFICAR USUARIOS DE GITHUB (pedir contraseña)*/

    @PutMapping("/github/owner/{username}") // PutTest
    public Map<String, Object> updateOwner(@PathVariable String username
            , @RequestParam String password, BindingResult bindingResult) {
        User oldUser = userService.findUserByUsername(username);
        gitHubUserAuthenticatedValidator.validateGitHubUserAuthenticated(password, oldUser, bindingResult);
        if (bindingResult.hasErrors())
            throw new BadRequestException("Password or token is not correct.");
        return dtoManager.getShowUserAsJson(gitHubConverter.turnOwnerIntoUser(ownerService.updateOwner(oldUser), EASY_PASSWORD));
    }
}
