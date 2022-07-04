package com.todolist.resources;

import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.model.ShowTask;
import com.todolist.model.ShowUser;
import com.todolist.parsers.UserParser;
import com.todolist.repository.Repositories;
import com.todolist.utilities.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserResource {

    @Autowired
    @Qualifier("repositories")
    private Repositories repositories;

    @Autowired
    @Qualifier("userParser")
    private UserParser userParser;

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
                                                 @RequestParam(required = false) @Pattern(regexp = "[<>=]{2}\\d+|[<>=]\\d+", message = "The task completed is invalid.") String taskCompleted) {

        List<ShowUser> result = new ArrayList<>(),
                users = userParser.parseList(
                        repositories.userRepository.findAll(Sort.by(order.charAt(0) == '-' ? Sort.Direction.DESC : Sort.Direction.ASC, order.charAt(0) == '+' || order.charAt(0) == '-' ? order.substring(1, order.length() - 1) : order)), repositories);
        int start = offset == null || offset < 1 ? 0 : offset - 1; // Donde va a comenzar.
        int end = limit == null || limit > users.size() ? users.size() : start + limit; // Donde va a terminar.
        for (int i = start; i < end; i++) {
            ShowUser user = users.get(i);
            if (user != null &&
                    (name == null || user.getName().equals(name)) &&
                    (surname == null || user.getSurname().equals(surname)) &&
                    (email == null || user.getEmail().equals(email)) &&
                    (location == null || user.getLocation().equals(location)) &&
                    (taskCompleted == null || Filter.isGEL(user.getTaskCompleted(), taskCompleted)))
                result.add(user);
        }
        return result.stream().map(user -> user.getFields(fieldsUser, fieldsTask)).collect(Collectors.toList());
    }

    @GetMapping("/{idUser}")
    public Map<String, Object> getUser(@PathVariable("idUser") @Min(value = 0, message = "The idUser must be positive.") Long idUser,
                                       @RequestParam(defaultValue = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration") String fieldsTask,
                                       @RequestParam(defaultValue = "idUser,name,surname,email,avatar,bio,location,taskCompleted,tasks") String fieldsUser) {
        User user = repositories.userRepository.findById(idUser).orElse(null);
        if (user == null)
            throw new NullPointerException("The user with idUser " + idUser + " does not exist.|/api/v1/users/" + idUser);
        if (!Arrays.stream(fieldsUser.split(",")).allMatch(field -> ShowUser.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase())))
            throw new IllegalArgumentException("The users' fields are invalid.|/api/v1/users/" + idUser);
        if (!Arrays.stream(fieldsTask.split(",")).allMatch(field -> ShowTask.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase())))
            throw new IllegalArgumentException("The tasks' fields are invalid.|/api/v1/users/" + idUser);
        return new ShowUser(user, repositories.getShowTaskFromUser(user)).getFields(fieldsUser, fieldsTask);
    }

    @PostMapping
    public Map<String, Object> addUser(@RequestBody @Valid User user) {
        if (user.getName() == null)
            throw new IllegalArgumentException("The user with idUser " + user.getIdUser() + " must have name.|/api/v1/users/");
        else if (user.getSurname() == null)
            throw new IllegalArgumentException("The user with idUser " + user.getIdUser() + " must have surname.|/api/v1/users/");
        else if (user.getEmail() == null)
            throw new IllegalArgumentException("The user with idUser " + user.getIdUser() + " must have email.|/api/v1/users/");
        repositories.userRepository.save(user);
        return new ShowUser(user, repositories.getShowTaskFromUser(user)).getFields(ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @PutMapping
    public Map<String, Object> updateUser(@RequestBody @Valid User user) {
        User oldUser = repositories.userRepository.findByIdUser(user.getIdUser());
        if (oldUser == null)
            throw new NullPointerException("The user with idUser " + user.getIdUser() + " does not exist.|/api/v1/users/" + user.getIdUser());
        if (user.getName() != null)
            oldUser.setName(user.getName());
        if (user.getSurname() != null)
            oldUser.setSurname(user.getSurname());
        if (user.getEmail() != null)
            oldUser.setEmail(user.getEmail());
        if (user.getAvatar() != null)
            oldUser.setAvatar(user.getAvatar());
        if (user.getBio() != null)
            oldUser.setBio(user.getBio());
        if (user.getLocation() != null)
            oldUser.setLocation(user.getLocation());
        repositories.userRepository.save(oldUser);
        return new ShowUser(oldUser, repositories.getShowTaskFromUser(oldUser)).getFields(ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @DeleteMapping("/{idUser}")
    public Map<String, Object> deleteUser(@PathVariable("idUser") @Min(value = 0, message = "The idGroup must be positive.") Long idUser) {
        User user = repositories.userRepository.findByIdUser(idUser);
        if (user == null)
            throw new NullPointerException("The user with idUser " + idUser + " does not exist.|/api/v1/users/" + idUser);
        repositories.userRepository.delete(user);
        return new ShowUser(user, repositories.getShowTaskFromUser(user)).getFields(ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @PostMapping("/{idUser}/tasks/{idTask}")
    public Map<String, Object> addTaskToUser(@PathVariable("idUser") Long idUser, @PathVariable("idTask") Long idTask) {
        User user = repositories.userRepository.findByIdUser(idUser);
        if (user == null)
            throw new NullPointerException("The user with idUser " + idUser + " does not exist.|/api/v1/users/" + idUser);
        Task task = repositories.taskRepository.findByIdTask(idTask);
        if (task == null)
            throw new NullPointerException("The task with idTask " + idTask + " does not exist.|/api/v1/tasks/" + idTask);
        repositories.addTaskToUser(user, task);
        return new ShowUser(user, repositories.getShowTaskFromUser(user)).getFields(ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @DeleteMapping("/{idUser}/tasks/{idTask}")
    public Map<String, Object> deleteTaskFromUser(@PathVariable("idUser") Long idUser, @PathVariable("idTask") Long idTask) {
        User user = repositories.userRepository.findByIdUser(idUser);
        if (user == null)
            throw new NullPointerException("The user with idUser " + idUser + " does not exist.|/api/v1/users/" + idUser);
        Task task = repositories.taskRepository.findByIdTask(idTask);
        if (task == null)
            throw new NullPointerException("The task with idTask " + idTask + " does not exist.|/api/v1/tasks/" + idTask);
        repositories.removeTaskFromUser(user, task);
        return new ShowUser(user, repositories.getShowTaskFromUser(user)).getFields(ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @DeleteMapping("/{idUser}/tasks")
    public Map<String, Object> deleteAllTasksFromUser(@PathVariable("idUser") Long idUser) {
        User user = repositories.userRepository.findByIdUser(idUser);
        if (user == null)
            throw new NullPointerException("The user with idUser " + idUser + " does not exist.|/api/v1/users/" + idUser);
        repositories.removeAllTasksFromUser(user);
        return new ShowUser(user, repositories.getShowTaskFromUser(user)).getFields(ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }
}
