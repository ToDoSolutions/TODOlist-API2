package com.todolist.services.user;

import com.todolist.dtos.ShowTask;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.exceptions.NotFoundException;
import com.todolist.repositories.UserRepository;
import com.todolist.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserTaskService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final TaskService taskService;

    @Autowired
    public UserTaskService(UserRepository userRepository, UserService userService, TaskService taskService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.taskService = taskService;
    }

    @Transactional
    public List<ShowTask> getShowTasksFromUser(User user) {
        return getTasksFromUser(user).stream().map(ShowTask::new).toList();
    }

    // Finders ----------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<User> findUsersWithTask(Task task) {
        List<User> users = userRepository.findAllByTaskId(task.getId());
        if (users.isEmpty())
            throw new NotFoundException("No users have the task with idTask " + task.getId() + ".");
        return users;
    }


    @Transactional(readOnly = true)
    public List<Task> getTasksFromUser(User user) {
        return userRepository.findAllTaskByUserId(user.getId());
    }

    // Save and delete --------------------------------------------------------
    @Transactional
    public void addTaskToUser(User user, Task task) {
        if (!user.getTasks().contains(task)) {
            task.setUser(user);
            taskService.saveTask(task);
            userService.saveUser(user);
        }
    }

    @Transactional
    public void removeTaskFromUser(User user, Task task) {
        user.getTasks().removeIf(t -> t.getId().equals(task.getId()));
        userService.saveUser(user);
    }

    @Transactional
    public void removeAllTasksFromUser(User user) {
        user.setTasks(null);
        userService.saveUser(user);
    }
}
