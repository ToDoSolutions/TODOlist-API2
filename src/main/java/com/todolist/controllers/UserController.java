package com.todolist.controllers;

import com.google.common.collect.Lists;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.exceptions.BadRequestException;
import com.todolist.exceptions.NotFoundException;
import com.todolist.filters.NumberFilter;
import com.todolist.services.TaskService;
import com.todolist.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;

import javax.validation.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.*;

@RestController
@Validated
@AllArgsConstructor
public class UserController {


    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator(); // Arreglar algún día.

    private TaskService taskService;

    private UserService userService;

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
        List<String> listTaskFields = List.of(ShowTask.ALL_ATTRIBUTES.toLowerCase().split(","));
        if (listUserFields.stream().noneMatch(prop -> prop.equalsIgnoreCase(propertyOrder)))
            throw new BadRequestException("The order is invalid.");
        if (!(Arrays.stream(fieldsUser.split(",")).allMatch(field -> listUserFields.contains(field.toLowerCase()))))
            throw new BadRequestException("The users' fields are invalid.");
        if (!(Arrays.stream(fieldsTask.split(",")).allMatch(field -> listTaskFields.contains(field.toLowerCase()))))
            throw new BadRequestException("The tasks' fields are invalid.");
        List<ShowUser> result = Lists.newArrayList(),
                users = userService.findAllShowUsers(Sort.by(order.charAt(0) == '-' ? Sort.Direction.DESC : Sort.Direction.ASC, propertyOrder));
        if (limit == -1) limit = users.size();
        int start = offset == null || offset < 1 ? 0 : offset - 1; // Donde va a comenzar.
        int end = limit > users.size() || start + limit > users.size() ? users.size() : start + limit; // Donde va a terminar.
        for (int i = start; i < end; i++) {
            ShowUser user = users.get(i);
            if (user != null &&
                    (name == null || user.getName().equals(name)) &&
                    (surname == null || user.getSurname().equals(surname)) &&
                    (email == null || user.getEmail().equals(email)) &&
                    (location == null || user.getLocation().equals(location)) &&
                    (taskCompleted == null || taskCompleted.isValid(user.getTaskCompleted())) &&
                    (bio == null || user.getBio().contains(bio)) &&
                    (avatar == null || user.getAvatar().equals(avatar)))
                result.add(user);
        }
        return result.stream().map(user -> user.getFields(fieldsUser, fieldsTask)).toList();
    }

    @GetMapping("/user/{idUser}") // GetSoloTest
    public Map<String, Object> getUser(@PathVariable("idUser") @Min(value = 0, message = "The idUser must be positive.") Long idUser,
                                       @RequestParam(defaultValue = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration") String fieldsTask,
                                       @RequestParam(defaultValue = "idUser,name,surname,email,avatar,bio,location,taskCompleted,tasks") String fieldsUser) {
        User user = userService.findUserById(idUser);
        if (user == null)
            throw new NotFoundException("The user with idUser " + idUser + " does not exist.");
        if (!(Arrays.stream(fieldsUser.split(",")).allMatch(field -> ShowUser.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase()))))
            throw new BadRequestException("The users' fields are invalid.");
        if (!(Arrays.stream(fieldsTask.split(",")).allMatch(field -> ShowTask.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase()))))
            throw new BadRequestException("The tasks' fields are invalid.");
        return new ShowUser(user, userService.getShowTaskFromUser(user)).getFields(fieldsUser, fieldsTask);
    }

    @PostMapping("/user") // PostTest
    public Map<String, Object> addUser(@RequestBody @Valid User user) {
        if (user == null)
            throw new BadRequestException("The user is null.");
        if (user.getName() == null || user.getName().isEmpty())
            throw new BadRequestException("The name is required.");
        if (user.getSurname() == null || user.getSurname().isEmpty())
            throw new BadRequestException("The surname is required.");
        if (user.getEmail() == null || user.getEmail().isEmpty())
            throw new BadRequestException("The email is required.");
        if (user.getAvatar() == null || user.getAvatar().isEmpty())
            throw new BadRequestException("The avatar is required.");
        if (user.getPassword() == null || user.getPassword().isEmpty())
            throw new BadRequestException("The password is required.");
        if (user.getToken() != null)
            throw new BadRequestException("The token is not required.");
        user = userService.saveUser(user);
        return new ShowUser(user, userService.getShowTaskFromUser(user)).getFields(ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @PutMapping("/user") // PutTest
    public Map<String, Object> updateUser(@RequestBody @Valid User user) {
        User oldUser = userService.findUserById(user.getIdUser());
        if (oldUser == null)
            throw new NotFoundException("The user with idUser " + user.getIdUser() + " does not exist.");
        if (user.getName() != null && !user.getName().isEmpty())
            oldUser.setName(user.getName());
        if (user.getSurname() != null && !user.getSurname().isEmpty())
            oldUser.setSurname(user.getSurname());
        if (user.getEmail() != null && !user.getEmail().isEmpty())
            oldUser.setEmail(user.getEmail());
        if (user.getAvatar() != null && !user.getAvatar().isEmpty())
            oldUser.setAvatar(user.getAvatar());
        if (user.getBio() != null && !user.getBio().isEmpty())
            oldUser.setBio(user.getBio());
        if (user.getLocation() != null && !user.getLocation().isEmpty())
            oldUser.setLocation(user.getLocation());
        if (user.getUsername() != null && !user.getUsername().isEmpty())
            oldUser.setUsername(user.getUsername());
        if (!Objects.equals(user.getPassword(), oldUser.getPassword()))
            throw new BadRequestException("The password is not required.");
        if (user.getToken() != null && !user.getToken().isEmpty())
            throw new BadRequestException("The token can't be updated with an UPDATE.");
        Set<ConstraintViolation<User>> errors = validator.validate(oldUser);
        if (!errors.isEmpty())
            throw new ConstraintViolationException(errors);
        oldUser = userService.saveUser(oldUser);
        return new ShowUser(oldUser, userService.getShowTaskFromUser(oldUser)).getFields(ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    /* TASK OPERATIONS */

    @DeleteMapping("/user/{idUser}/tasks") // DeleteTest
    public Map<String, Object> deleteAllTasksFromUser(@PathVariable("idUser") Long idUser) {
        User user = userService.findUserById(idUser);
        if (user == null)
            throw new NotFoundException("The user with idUser " + idUser + " does not exist.");
        userService.removeAllTasksFromUser(user);
        return new ShowUser(user, userService.getShowTaskFromUser(user)).getFields(ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @DeleteMapping("/user/{idUser}/task/{idTask}") // DeleteAllTest
    public Map<String, Object> deleteTaskFromUser(@PathVariable("idUser") Long idUser, @PathVariable("idTask") Long idTask) {
        User user = userService.findUserById(idUser);
        if (user == null)
            throw new NotFoundException("The user with idUser " + idUser + " does not exist.");
        Task task = taskService.findTaskById(idTask);
        if (task == null)
            throw new NotFoundException("The task with idTask " + idTask + " does not exist.");
        if (userService.getTasksFromUser(user).contains(task)) userService.removeTaskFromUser(user, task);
        return new ShowUser(user, userService.getShowTaskFromUser(user)).getFields(ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @GetMapping("/users/task/{idTask}") // GetAllTest
    public List<Map<String, Object>> getUserWithTask(@PathVariable("idTask") @Min(value = 0, message = "The idTask must be positive.") Long idTask) {
        Task task = taskService.findTaskById(idTask);
        if (task == null)
            throw new NotFoundException("The task with idTask " + idTask + " does not exist.");
        List<User> users = userService.findUsersWithTask(task);
        if (users == null || users.isEmpty())
            throw new BadRequestException("The task with idTask " + idTask + " does not belong to any user.");
        return users.stream().map(user -> new ShowUser(user, userService.getShowTaskFromUser(user)).getFields(ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES)).toList();
    }

    @PutMapping("/user/{idUser}/task/{idTask}") // PutTest
    public Map<String, Object> addTaskToUser(@PathVariable("idUser") Long idUser, @PathVariable("idTask") Long idTask) {
        User user = userService.findUserById(idUser);
        if (user == null)
            throw new NotFoundException("The user with idUser " + idUser + " does not exist.");
        Task task = taskService.findTaskById(idTask);
        if (task == null)
            throw new NotFoundException("The task with idTask " + idTask + " does not exist.");
        if (!userService.getTasksFromUser(user).contains(task)) userService.addTaskToUser(user, task);
        return new ShowUser(user, userService.getShowTaskFromUser(user)).getFields(ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    /* TOKEN OPERATION */

    @SuppressWarnings("unchecked")
    @PutMapping("/user/{idUser}/token") // TokenTest
    public Map<String, Object> updateToken(@PathVariable("idUser") @Min(value = 0, message = "The idUser must be positive.") Long idUser, ServletWebRequest request) {
        User user = userService.findUserById(idUser);
        if (user == null)
            throw new NotFoundException("The user with idUser " + idUser + " does not exist.");
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty())
            throw new BadRequestException("The token is required.");
        if (token.contains("Bearer")) token = token.replace("Bearer", "").trim();
        user.setToken(token);
        user = userService.saveUser(user);
        return new ShowUser(user, userService.getShowTaskFromUser(user)).getFields(ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }


}
