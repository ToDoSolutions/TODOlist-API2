package com.todolist.controllers.user;

import com.todolist.dtos.ShowUser;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.services.TaskService;
import com.todolist.services.user.UserService;
import com.todolist.services.user.UserTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserTaskController {

    // Services ---------------------------------------------------------------
    private final TaskService taskService;
    private final UserService userService;
    private final UserTaskService userTaskService;

    /* ------------ */
    // CRUD Methods //
    /* ------------ */

    // Getters -----------------------------------------------------------------
    @GetMapping("/users/task/{idTask}")
    public ResponseEntity<List<ShowUser>> getUserWithTask(@PathVariable("idTask") @Min(value = 0, message = "The idTask must be positive.") Integer idTask) {
        Task task = taskService.findTaskById(idTask);
        List<User> users = userTaskService.findUsersWithTask(task);
        List<ShowUser> showUsers = users.stream().map(user -> new ShowUser(user, userTaskService.getShowTasksFromUser(user))).toList();
        return ResponseEntity.ok(showUsers);
    }

    // Adders ------------------------------------------------------------------
    @PutMapping("/user/{idUser}/task/{idTask}")
    public ResponseEntity<ShowUser> addTaskToUser(@PathVariable("idUser") Integer idUser, @PathVariable("idTask") Integer idTask) {
        User user = userService.findUserById(idUser);
        Task task = taskService.findTaskById(idTask);
        if (!user.getTasks().contains(task)) userTaskService.addTaskToUser(user, task);
        ShowUser showUser = new ShowUser(user, userTaskService.getShowTasksFromUser(user));
        return ResponseEntity.ok(showUser);
    }

    // Deleters ----------------------------------------------------------------
    @DeleteMapping("/user/{idUser}/tasks")
    public ResponseEntity<ShowUser> deleteAllTasksFromUser(@PathVariable("idUser") Integer idUser) {
        User user = userService.findUserById(idUser);
        userTaskService.removeAllTasksFromUser(user);
        ShowUser showUser = new ShowUser(user, userTaskService.getShowTasksFromUser(user));
        return ResponseEntity.ok(showUser);
    }

    @DeleteMapping("/user/{idUser}/task/{idTask}")
    public ResponseEntity<ShowUser> deleteTaskFromUser(@PathVariable("idUser") Integer idUser, @PathVariable("idTask") Integer idTask) {
        User user = userService.findUserById(idUser);
        Task task = taskService.findTaskById(idTask);
        if (user.getTasks().contains(task)) userTaskService.removeTaskFromUser(user, task);
        ShowUser showUser = new ShowUser(user, userTaskService.getShowTasksFromUser(user));
        return ResponseEntity.ok(showUser);
    }
}
