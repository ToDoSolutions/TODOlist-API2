package com.todolist.controllers;

import com.google.common.collect.Lists;
import com.todolist.component.DTOManager;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.exceptions.BadRequestException;
import com.todolist.exceptions.NotFoundException;
import com.todolist.filters.NumberFilter;
import com.todolist.services.TaskService;
import com.todolist.services.UserService;
import com.todolist.entity.IterableEntity;
import com.todolist.validators.FieldValidator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
    public Map<String, Object> deleteUser(@PathVariable("idUser") @Min(value = 0, message = "The idGroup must be positive.") Long idUser) {
        User user = userService.findUserById(idUser);
        if (user == null)
            throw new NotFoundException("The user with idUser " + idUser + " does not exist.");
        userService.deleteUser(user);
        return new ShowUser(user, userService.getShowTaskFromUser(user)).getFields(ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @GetMapping("/users") // GetAllTest
    public List<Map<String, Object>> getAllUsers(@RequestParam(defaultValue = "0") @Min(value = 0, message = "The offset must be positive.") Integer offset,
                                                 @RequestParam(defaultValue = Integer.MAX_VALUE + "") @Min(value = 0, message = "The limit must be positive.") Integer limit,
                                                 @RequestParam(defaultValue = "idUser") String order,
                                                 @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES) String fieldsTask,
                                                 @RequestParam(defaultValue = ShowUser.ALL_ATTRIBUTES) String fieldsUser,
                                                 @RequestParam(required = false) String name,
                                                 @RequestParam(required = false) String surname,
                                                 @RequestParam(required = false) @Email(message = "The email is invalid.") String email,
                                                 @RequestParam(required = false) @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z\\d+&@#/%?=~_|!:,.;]*[-a-zA-Z\\d+&@#/%=~_|]", message = "The avatar is invalid.") String avatar,
                                                 @RequestParam(required = false) String bio,
                                                 @RequestParam(required = false) String location,
                                                 @RequestParam(required = false) NumberFilter taskCompleted) {
        String propertyOrder = order.charAt(0) == '+' || order.charAt(0) == '-' ? order.substring(1) : order;
        List<String> listUserFields = List.of(ShowUser.ALL_ATTRIBUTES.toLowerCase().split(","));
        if (listUserFields.stream().noneMatch(prop -> prop.equalsIgnoreCase(propertyOrder)))
            throw new BadRequestException("The order is invalid.");
        List<User> result = Lists.newArrayList(),
                users = userService.findAllShowUsers(Sort.by(order.charAt(0) == '-' ? Sort.Direction.DESC : Sort.Direction.ASC, propertyOrder));
        IterableEntity<User> iterableEntity = new IterableEntity<>(users, limit, offset);
        for (User user : iterableEntity) {
            if (user != null &&
                    (name == null || user.getName().equals(name)) &&
                    (surname == null || user.getSurname().equals(surname)) &&
                    (email == null || user.getEmail().equals(email)) &&
                    (location == null || user.getLocation().equals(location)) &&
                    (taskCompleted == null || taskCompleted.isValid(userService.getTaskCompleted(user))) &&
                    (bio == null || user.getBio().contains(bio)) &&
                    (avatar == null || user.getAvatar().equals(avatar)))
                result.add(user);
        }
        return result.stream().map(user -> dtoManager.getShowUserAsJson(user, fieldsUser, fieldsTask)).toList();
    }

    @GetMapping("/user/{idUser}") // GetSoloTest
    public Map<String, Object> getUser(@PathVariable("idUser") @Min(value = 0, message = "The idUser must be positive.") Long idUser,
                                       @RequestParam(defaultValue = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration") String fieldsTask,
                                       @RequestParam(defaultValue = "idUser,name,surname,email,avatar,bio,location,taskCompleted,tasks") String fieldsUser) {
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
        User oldUser = userService.findUserById(user.getIdUser());
        BeanUtils.copyProperties(user, oldUser, "idUser", "password", "token", "tasks");
        oldUser = userService.saveUser(oldUser);
        return dtoManager.getShowUserAsJson(oldUser);
    }

    /* TASK OPERATIONS */

    @DeleteMapping("/user/{idUser}/tasks") // DeleteTest
    public Map<String, Object> deleteAllTasksFromUser(@PathVariable("idUser") Long idUser) {
        User user = userService.findUserById(idUser);
        userService.removeAllTasksFromUser(user);
        return dtoManager.getShowUserAsJson(user);
    }

    @DeleteMapping("/user/{idUser}/task/{idTask}") // DeleteAllTest
    public Map<String, Object> deleteTaskFromUser(@PathVariable("idUser") Long idUser, @PathVariable("idTask") Long idTask) {
        User user = userService.findUserById(idUser);
        Task task = taskService.findTaskById(idTask);
        if (userService.getTasksFromUser(user).contains(task)) userService.removeTaskFromUser(user, task);
        return dtoManager.getShowUserAsJson(user);
    }

    @GetMapping("/users/task/{idTask}") // GetAllTest
    public List<Map<String, Object>> getUserWithTask(@PathVariable("idTask") @Min(value = 0, message = "The idTask must be positive.") Long idTask) {
        Task task = taskService.findTaskById(idTask);
        List<User> users = userService.findUsersWithTask(task);
        return users.stream().map(dtoManager::getShowUserAsJson).toList();
    }

    @PutMapping("/user/{idUser}/task/{idTask}") // PutTest
    public Map<String, Object> addTaskToUser(@PathVariable("idUser") Long idUser, @PathVariable("idTask") Long idTask) {
        User user = userService.findUserById(idUser);
        Task task = taskService.findTaskById(idTask);
        if (!userService.getTasksFromUser(user).contains(task)) userService.addTaskToUser(user, task);
        return dtoManager.getShowUserAsJson(user);
    }

    /* TOKEN OPERATION */

    @PutMapping("/user/{idUser}/token") // TokenTest
    public Map<String, Object> updateToken(@PathVariable("idUser") @Min(value = 0, message = "The idUser must be positive.") Long idUser,
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
