package com.todolist.controllers.github;

import com.todolist.dtos.ShowUser;
import com.todolist.entity.User;
import com.todolist.exceptions.BadRequestException;
import com.todolist.exceptions.NotFoundException;
import com.todolist.services.UserService;
import com.todolist.services.github.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
@Validated
public class OwnerController {

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private UserService userService;

    /* OBTENER INFORMACIÓN Y MODIFICAR BASE DE DATOS */

    // Obtener usuario de GitHub (ya existente)
    @GetMapping("/github/user/{username}") // GetSoloTest
    public ShowUser getOwner(@PathVariable String username) {
        return ownerService.turnOwnerIntoShowUser(ownerService.findByUsername(username));
    }

    @PutMapping("/github/user/{username}") // PutTest
    public ShowUser updateUser(@PathVariable String username) {
        User oldUser = userService.findUserByUsername(username);
        if (oldUser == null)
            throw new NotFoundException("User not found");
        return new ShowUser(ownerService.updateUser(oldUser), userService.getShowTaskFromUser(oldUser));
    }

    /* MODIFICAR USUARIOS DE GITHUB (pedir contraseña)*/

    @PutMapping("/github/owner/{username}") // PutTest
    public ShowUser updateOwner(@PathVariable String username
            ,@RequestParam String password) {
        User oldUser = userService.findUserByUsername(username);
        if (oldUser == null)
            throw new NotFoundException("User not found");
        if (oldUser.getToken() == null)
            throw new BadRequestException("The token is needed");
        if (!Objects.equals(oldUser.getPassword(), password))
            throw new BadRequestException("The password is incorrect");
        return ownerService.turnOwnerIntoShowUser(ownerService.updateOwner(oldUser));
    }
}
