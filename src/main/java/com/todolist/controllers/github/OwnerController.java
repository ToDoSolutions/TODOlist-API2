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

import javax.validation.constraints.Min;
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
    @GetMapping("/github/user/{idUser}") // GetSoloTest
    public ShowUser getOwner(@PathVariable @Min(value = 0, message = "The idUser must be positive.") Long idUser) {
        return ownerService.turnOwnerIntoShowUser(ownerService.findById(idUser));
    }

    @PutMapping("/github/user/{idUser}") // PutTest
    public ShowUser updateUser(@PathVariable @Min(value = 0, message = "The idUser must be positive.") Long idUser) {
        User oldUser = userService.findUserById(idUser);
        if (oldUser == null)
            throw new NotFoundException("User not found");
        return new ShowUser(ownerService.updateUser(oldUser), userService.getShowTaskFromUser(oldUser));
    }

    /* MODIFICAR USUARIOS DE GITHUB (pedir contraseña)*/

    @PutMapping("/github/owner/{idUser}") // PutTest
    public ShowUser updateOwner(@PathVariable @Min(value = 0, message = "The idUser must be positive.") Long idUser
            ,@RequestParam String password) {
        User oldUser = userService.findUserById(idUser);
        if (oldUser == null)
            throw new NotFoundException("User not found");
        if (oldUser.getToken() == null)
            throw new BadRequestException("The token is needed");
        if (!Objects.equals(oldUser.getPassword(), password))
            throw new BadRequestException("The password is incorrect");
        return ownerService.turnOwnerIntoShowUser(ownerService.updateOwner(oldUser));
    }
}
