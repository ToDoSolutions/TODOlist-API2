package com.todolist.controllers.user;

import com.todolist.dtos.ShowUser;
import com.todolist.entity.User;
import com.todolist.exceptions.BadRequestException;
import com.todolist.services.user.UserService;
import com.todolist.services.user.UserTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Validator;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserTokenController {

    // Services ---------------------------------------------------------------
    private final UserService userService;
    private final UserTaskService userTaskService;

    // Validators -------------------------------------------------------------
    private final Validator validator;

    /* ------------ */
    // CRUD Methods //
    /* ------------ */

    // Getters -----------------------------------------------------------------
    @GetMapping("/user/{idUser}/token")
    public ResponseEntity<String> getTokenFromUser(@PathVariable("idUser") @Min(value = 0, message = "The idUser must be positive.") Integer idUser, @RequestHeader("Password") String password) {

        User user = userService.findUserById(idUser);
        if (password == null || password.isEmpty())
            throw new BadRequestException("The password is required.");
        if (!user.getPassword().equals(password))
            throw new BadRequestException("The password is incorrect.");
        return ResponseEntity.ok(user.getToken());
    }

    // Updaters ----------------------------------------------------------------
    // TODO: Check if the token is valid.
    @PutMapping("/user/{idUser}/token")
    public ResponseEntity<ShowUser> updateToken(@PathVariable("idUser") @Min(value = 0, message = "The idUser must be positive.") Integer idUser,
                                                @RequestHeader("Authorization") String token) {
        User user = userService.findUserById(idUser);
        if (token == null || token.isEmpty())
            throw new BadRequestException("The token is required.");
        if (token.contains("Bearer")) token = token.replace("Bearer", "").trim();
        validator.validate(user);
        user.setToken(token);
        user = userService.saveUser(user);
        ShowUser showUser = new ShowUser(user, userTaskService.getShowTasksFromUser(user));

        return ResponseEntity.ok(showUser);
    }
}
