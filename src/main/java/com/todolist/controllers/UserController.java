package com.todolist.controllers;

import com.google.common.base.Preconditions;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.filters.FilterNumber;
import com.todolist.services.TaskService;
import com.todolist.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.*;

@RestController
@RequestMapping("/api/v1/users")
@Validated
@AllArgsConstructor
public class UserController {


    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator(); // Arreglar algún día.

    private TaskService taskService;

    private UserService userService;

    @GetMapping
    public List<Map<String, Object>> getAllUsers(@RequestParam(defaultValue = "0") @Min(value = 0, message = "The offset must be positive.") Integer offset,
                                                 @RequestParam(defaultValue = Integer.MAX_VALUE + "") @Min(value = 0, message = "The limit must be positive") Integer limit,
                                                 @RequestParam(defaultValue = "idUser") String order,
                                                 @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES) String fieldsTask,
                                                 @RequestParam(defaultValue = ShowUser.ALL_ATTRIBUTES) String fieldsUser,
                                                 @RequestParam(required = false) String name,
                                                 @RequestParam(required = false) String surname,
                                                 @RequestParam(required = false) @Email(message = "The email is invalid.") String email,
                                                 @RequestParam(required = false) @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z\\d+&@#/%?=~_|!:,.;]*[-a-zA-Z\\d+&@#/%=~_|]", message = "The avatar is invalid.") String avatar,
                                                 @RequestParam(required = false) String bio,
                                                 @RequestParam(required = false) String location,
                                                 @RequestParam(required = false) FilterNumber taskCompleted) {
        String propertyOrder = order.charAt(0) == '+' || order.charAt(0) == '-' ? order.substring(1) : order;
        Preconditions.checkArgument(Arrays.stream(ShowTask.ALL_ATTRIBUTES.split(",")).anyMatch(prop -> prop.equalsIgnoreCase(propertyOrder)), "The order is invalid.");
        Preconditions.checkArgument(Arrays.stream(fieldsUser.split(",")).allMatch(field -> ShowTask.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase())), "The fields are invalid.");
        List<ShowUser> result = new ArrayList<>(),
                users = userService.findAllShowUsers(Sort.by(order.charAt(0) == '-' ? Sort.Direction.DESC : Sort.Direction.ASC, propertyOrder));
        if (limit == -1) limit = users.size() - 1;
        int start = offset == null || offset < 1 ? 0 : offset - 1; // Donde va a comenzar.
        int end = limit > users.size() ? users.size() : start + limit; // Donde va a terminar.
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

    @GetMapping("/{idUser}")
    public Map<String, Object> getUser(@PathVariable("idUser") @Min(value = 0, message = "The idUser must be positive.") Long idUser,
                                       @RequestParam(defaultValue = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration") String fieldsTask,
                                       @RequestParam(defaultValue = "idUser,name,surname,email,avatar,bio,location,taskCompleted,tasks") String fieldsUser) {
        User user = userService.findUserById(idUser);
        Preconditions.checkNotNull(user, "The user with idUser " + idUser + " does not exist.");
        Preconditions.checkArgument(Arrays.stream(fieldsUser.split(",")).allMatch(field -> ShowUser.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase())), "The users' fields are invalid.");
        Preconditions.checkArgument(Arrays.stream(fieldsTask.split(",")).allMatch(field -> ShowTask.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase())), "The tasks' fields are invalid.");
        return new ShowUser(user, userService.getShowTaskFromUser(user)).getFields(fieldsUser, fieldsTask);
    }

    @PostMapping
    public Map<String, Object> addUser(@RequestBody @Valid User user) {
        Preconditions.checkNotNull(user, "The user is null.");
        Preconditions.checkArgument(user.getUsername() != null && !Objects.equals(user.getUsername(), ""), "The username is required.");
        Preconditions.checkArgument(user.getName() != null && !Objects.equals(user.getName(), ""), "The name is required.");
        Preconditions.checkArgument(user.getSurname() != null && !Objects.equals(user.getSurname(), ""), "The surname is required.");
        Preconditions.checkArgument(user.getEmail() != null && !Objects.equals(user.getEmail(), ""), "The email is required.");
        Preconditions.checkArgument(user.getPassword() != null && !Objects.equals(user.getPassword(), ""), "The password is required.");
        Preconditions.checkArgument(user.getAvatar() != null && !Objects.equals(user.getAvatar(), ""), "The avatar is required.");
        Preconditions.checkArgument(user.getToken() == null, "The token can't be added with an CREATE.");
        user = userService.saveUser(user);
        return new ShowUser(user, userService.getShowTaskFromUser(user)).getFields(ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @PutMapping
    public Map<String, Object> updateUser(@RequestBody @Valid User user) {
        User oldUser = userService.findUserById(user.getIdUser());
        Preconditions.checkNotNull(oldUser, "The user with idUser " + user.getIdUser() + " does not exist.");
        if (user.getName() != null && !Objects.equals(user.getName(), ""))
            oldUser.setName(user.getName());
        if (user.getSurname() != null && !Objects.equals(user.getSurname(), ""))
            oldUser.setSurname(user.getSurname());
        if (user.getEmail() != null && !Objects.equals(user.getEmail(), ""))
            oldUser.setEmail(user.getEmail());
        if (user.getAvatar() != null && !Objects.equals(user.getAvatar(), ""))
            oldUser.setAvatar(user.getAvatar());
        if (user.getBio() != null && !Objects.equals(user.getBio(), ""))
            oldUser.setBio(user.getBio());
        if (user.getLocation() != null && !Objects.equals(user.getLocation(), ""))
            oldUser.setLocation(user.getLocation());
        if (user.getUsername() != null && !Objects.equals(user.getUsername(), ""))
            oldUser.setUsername(user.getUsername());
        Preconditions.checkArgument(Objects.equals(user.getPassword(), oldUser.getPassword()), "The password is required.");
        Preconditions.checkArgument(user.getToken() == null, "The token can't be updated with an UPDATE.");
        Set<ConstraintViolation<User>> errors = validator.validate(oldUser);
        if (!errors.isEmpty())
            throw new ConstraintViolationException(errors);
        oldUser = userService.saveUser(oldUser);
        return new ShowUser(oldUser, userService.getShowTaskFromUser(oldUser)).getFields(ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @DeleteMapping("/{idUser}")
    public Map<String, Object> deleteUser(@PathVariable("idUser") @Min(value = 0, message = "The idGroup must be positive.") Long idUser) {
        User user = userService.findUserById(idUser);
        Preconditions.checkNotNull(user, "The user with idUser " + idUser + " does not exist.");
        userService.deleteUser(user);
        return new ShowUser(user, userService.getShowTaskFromUser(user)).getFields(ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @PostMapping("/{idUser}/tasks/{idTask}")
    public Map<String, Object> addTaskToUser(@PathVariable("idUser") Long idUser, @PathVariable("idTask") Long idTask) {
        User user = userService.findUserById(idUser);
        Preconditions.checkNotNull(user, "The user with idUser " + idUser + " does not exist.");
        Task task = taskService.findTaskById(idTask);
        Preconditions.checkNotNull(task, "The task with idTask " + idTask + " does not exist.");
        userService.addTaskToUser(user, task);
        return new ShowUser(user, userService.getShowTaskFromUser(user)).getFields(ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @DeleteMapping("/{idUser}/tasks/{idTask}")
    public Map<String, Object> deleteTaskFromUser(@PathVariable("idUser") Long idUser, @PathVariable("idTask") Long idTask) {
        User user = userService.findUserById(idUser);
        Preconditions.checkNotNull(user, "The user with idUser " + idUser + " does not exist.");
        Task task = taskService.findTaskById(idTask);
        Preconditions.checkNotNull(task, "The task with idTask " + idTask + " does not exist.");
        userService.removeTaskFromUser(user, task);
        return new ShowUser(user, userService.getShowTaskFromUser(user)).getFields(ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @DeleteMapping("/{idUser}/tasks")
    public Map<String, Object> deleteAllTasksFromUser(@PathVariable("idUser") Long idUser) {
        User user = userService.findUserById(idUser);
        Preconditions.checkNotNull(user, "The user with idUser " + idUser + " does not exist.");
        userService.removeAllTasksFromUser(user);
        return new ShowUser(user, userService.getShowTaskFromUser(user)).getFields(ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @PutMapping("/{idUser}/token/{token}")
    public Map<String, Object> updateToken(@PathVariable("idUser") Long idUser, @PathVariable("token") String token) {
        User user = userService.findUserById(idUser);
        Preconditions.checkNotNull(user, "The user with idUser " + idUser + " does not exist.");
        user.setToken(token);
        user = userService.saveUser(user);
        return new ShowUser(user, userService.getShowTaskFromUser(user)).getFields(ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }
}
