package com.todolist.controllers.user;

import com.todolist.dtos.ShowUser;
import com.todolist.entity.User;
import com.todolist.exceptions.BadRequestException;
import com.todolist.services.user.UserService;
import com.todolist.services.user.UserTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Validator;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/api/v1")
public class UserTokenController {

    // Validators -------------------------------------------------------------
    private final Validator validator;

    // Services ---------------------------------------------------------------

    private final UserService userService;
    private final UserTaskService userTaskService;


    @Autowired
    public UserTokenController(Validator validator, UserService userService, UserTaskService userTaskService) {
        this.validator = validator;
        this.userService = userService;
        this.userTaskService = userTaskService;
    }

    @PutMapping("/user/{idUser}/token") // TokenTest
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
