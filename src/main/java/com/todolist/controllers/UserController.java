package com.todolist.controllers;

import com.fadda.common.Preconditions;
import com.fadda.iterables.iterator.IterableRangeObject;
import com.todolist.component.DTOManager;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.exceptions.BadRequestException;
import com.todolist.filters.NumberFilter;
import com.todolist.services.TaskService;
import com.todolist.services.UserService;
import com.todolist.dtos.Order;
import com.todolist.validators.FieldValidator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
@Validated
public class UserController {

    private final Validator validator;
    private final TaskService taskService;

    private final UserService userService;
    private final FieldValidator fieldValidator;
    private final DTOManager dtoManager;


    @Autowired
    public UserController(Validator validator, TaskService taskService, UserService userService, FieldValidator fieldValidator, DTOManager dtoManager) {
        this.validator = validator;
        this.taskService = taskService;
        this.userService = userService;
        this.fieldValidator = fieldValidator;
        this.dtoManager = dtoManager;
    }

    /* USER OPERATIONS */

    @DeleteMapping("/user/{idUser}") // DeleteTest
    public Map<String, Object> deleteUser(@PathVariable("idUser") Integer idUser) {
        User user = userService.findUserById(idUser);
        userService.deleteUser(user);
        return dtoManager.getShowUserAsJson(user);
    }

    @GetMapping("/users") // GetAllTest
    public List<Map<String, Object>> getAllUsers(@RequestParam(defaultValue = "0") @Min(value = 0, message = "The offset must be positive.") Integer offset,
                                                 @RequestParam(defaultValue = Integer.MAX_VALUE + "") @Min(value = 0, message = "The limit must be positive.") Integer limit,
                                                 @RequestParam(defaultValue = "+idUser") Order order,
                                                 @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES_STRING) String fieldsTask,
                                                 @RequestParam(defaultValue = ShowUser.ALL_ATTRIBUTES_STRING) String fieldsUser,
                                                 @RequestParam(required = false) String name,
                                                 @RequestParam(required = false) String surname,
                                                 @RequestParam(required = false) @Email(message = "The email is invalid.") String email,
                                                 @RequestParam(required = false) @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z\\d+&@#/%?=~_|!:,.;]*[-a-zA-Z\\d+&@#/%=~_|]", message = "The avatar is invalid.") String avatar,
                                                 @RequestParam(required = false) String bio,
                                                 @RequestParam(required = false) String location,
                                                 @RequestParam(required = false) NumberFilter taskCompleted) {
        order.validateOrder(fieldsUser);
        List<User> users = userService.findAllUsers(order.getSort());
        List<User> result = new IterableRangeObject<>(users, limit, offset)
                .stream().filter(user -> Objects.nonNull(user) &&
                        Preconditions.isNullOrValid(name, n -> user.getName().equals(n)) &&
                        Preconditions.isNullOrValid(surname, s -> user.getSurname().equals(s)) &&
                        Preconditions.isNullOrValid(email, e -> user.getEmail().equals(e)) &&
                        Preconditions.isNullOrValid(location, l -> user.getLocation().equals(l)) &&
                        Preconditions.isNullOrValid(taskCompleted, t -> t.isValid(userService.getTaskCompleted(user))) &&
                        Preconditions.isNullOrValid(bio, b -> user.getBio().contains(b)) &&
                        Preconditions.isNullOrValid(avatar, a -> user.getAvatar().equals(a))).toList();
        return result.stream().map(user -> dtoManager.getShowUserAsJson(user, fieldsUser, fieldsTask)).toList();
    }

    @GetMapping("/user/{idUser}") // GetSoloTest
    public Map<String, Object> getUser(@PathVariable("idUser") @Min(value = 0, message = "The idUser must be positive.") Integer idUser,
                                       @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES_STRING) String fieldsTask,
                                       @RequestParam(defaultValue = ShowUser.ALL_ATTRIBUTES_STRING) String fieldsUser) {
        User user = userService.findUserById(idUser);
        fieldValidator.userFieldValidate(fieldsUser);
        fieldValidator.taskFieldValidate(fieldsTask);
        return dtoManager.getShowUserAsJson(user, fieldsUser, fieldsTask);
    }

    @PostMapping("/user") // PostTest
    public Map<String, Object> addUser(@RequestBody @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new BadRequestException("The user is invalid.");
        user = userService.saveUser(user);
        return dtoManager.getShowUserAsJson(user);
    }

    @PutMapping("/user") // PutTest
    public Map<String, Object> updateUser(@RequestBody @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new BadRequestException("The user is invalid.");
        User oldUser = userService.findUserById(user.getId());
        BeanUtils.copyProperties(user, oldUser, "idUser", "password", "token", "tasks");
        oldUser = userService.saveUser(oldUser);
        return dtoManager.getShowUserAsJson(oldUser);
    }

    /* TASK OPERATIONS */

    @DeleteMapping("/user/{idUser}/tasks") // DeleteTest
    public Map<String, Object> deleteAllTasksFromUser(@PathVariable("idUser") Integer idUser) {
        User user = userService.findUserById(idUser);
        userService.removeAllTasksFromUser(user);
        return dtoManager.getShowUserAsJson(user);
    }

    @DeleteMapping("/user/{idUser}/task/{idTask}") // DeleteAllTest
    public Map<String, Object> deleteTaskFromUser(@PathVariable("idUser") Integer idUser, @PathVariable("idTask") Integer idTask) {
        User user = userService.findUserById(idUser);
        Task task = taskService.findTaskById(idTask);
        if (user.getTasks().contains(task)) userService.removeTaskFromUser(user, task);
        return dtoManager.getShowUserAsJson(user);
    }

    @GetMapping("/users/task/{idTask}") // GetAllTest
    public List<Map<String, Object>> getUserWithTask(@PathVariable("idTask") @Min(value = 0, message = "The idTask must be positive.") Integer idTask) {
        Task task = taskService.findTaskById(idTask);
        List<User> users = userService.findUsersWithTask(task);
        return users.stream().map(dtoManager::getShowUserAsJson).toList();
    }

    @PutMapping("/user/{idUser}/task/{idTask}") // PutTest
    public Map<String, Object> addTaskToUser(@PathVariable("idUser") Integer idUser, @PathVariable("idTask") Integer idTask) {
        User user = userService.findUserById(idUser);
        Task task = taskService.findTaskById(idTask);
        if (!user.getTasks().contains(task)) userService.addTaskToUser(user, task);
        return dtoManager.getShowUserAsJson(user);
    }

    /* TOKEN OPERATION */

    @PutMapping("/user/{idUser}/token") // TokenTest
    public Map<String, Object> updateToken(@PathVariable("idUser") @Min(value = 0, message = "The idUser must be positive.") Integer idUser,
                                           @RequestHeader("Authorization") String token) {
        User user = userService.findUserById(idUser);
        if (token == null || token.isEmpty())
            throw new BadRequestException("The token is required.");
        if (token.contains("Bearer")) token = token.replace("Bearer", "").trim();
        validator.validate(user);
        user.setToken(token);
        user = userService.saveUser(user);
        return dtoManager.getShowUserAsJson(user);
    }
}
