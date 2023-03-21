package com.todolist.services;

import com.todolist.component.DataManager;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.Status;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.entity.UserTask;
import com.todolist.exceptions.NotFoundException;
import com.todolist.repositories.UserRepository;
import com.todolist.repositories.UserTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Service
public class UserService {

    // Repositories -----------------------------------------------------------
    private final UserRepository userRepository;
    private final UserTaskRepository userTaskRepository;

    // Services ---------------------------------------------------------------

    private final TaskService taskService;


    // Components -------------------------------------------------------------
    private final DataManager dataManager;

    // Constructors -----------------------------------------------------------
    @Autowired
    public UserService(UserRepository userRepository, TaskService taskService, UserTaskRepository userTaskRepository, DataManager dataManager) {
        this.userRepository = userRepository;
        this.taskService = taskService;
        this.userTaskRepository = userTaskRepository;
        this.dataManager = dataManager;
    }

    // Populate database ------------------------------------------------------
    @PostConstruct
    @Transactional
    public void init() throws IOException {
        List<User> users = dataManager.loadUser();
        userRepository.saveAll(users);
    }

    /**
     * USERS
     */

    // Finders ----------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<User> findAllUsers(Sort sort) {
        return userRepository.findAll(sort);
    }

    @Transactional(readOnly = true)
    public User findUserById(Integer idUser) {
        return userRepository.findById(idUser).orElseThrow(() -> new NotFoundException("The user with idUser " + idUser + " does not exist."));
    }

    @Transactional(readOnly = true)
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("The user with username " + username + " does not exist."));
    }

    @Transactional
    public Long getTaskCompleted(User user) {
        return getTasksFromUser(user).stream().filter(task -> task.getStatus().equals(Status.DONE)).count();
    }

    // Save and delete --------------------------------------------------------
    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    /**
     * TASKS
     */
    // Finders ----------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<User> findUsersWithTask(Task task) {
        List<User> users = userRepository.findAll().stream().filter(user -> getTasksFromUser(user).contains(task)).toList();
        if (users.isEmpty())
            throw new NotFoundException("No users have the task with idTask " + task.getId() + ".");
        return users;
    }

    @Transactional
    public Task findTaskByTitle(String username, String title) {
        User user = findUserByUsername(username);
        return getTasksFromUser(user).stream().filter(task -> task.getTitle().equals(title)).findFirst().orElseThrow(() -> new NotFoundException("The task with title " + title + " does not exist."));
    }

    @Transactional(readOnly = true)
    public List<Task> getTasksFromUser(User user) {
        return userTaskRepository.findByIdUser(user.getId()).stream()
                .map(userTask -> taskService.findTaskById(userTask.getIdTask()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ShowTask> getShowTaskFromUser(User user) {
        return getTasksFromUser(user).stream().map(ShowTask::new).toList();
    }

    // Save and delete --------------------------------------------------------
    @Transactional
    public void addTaskToUser(User user, Task task) {
        userTaskRepository.save(new UserTask(user.getId(), task.getId()));
    }
    @Transactional
    public void removeTaskFromUser(User user, Task task) {
        List<UserTask> userTask = userTaskRepository.findByIdAndIdUser(task.getId(), user.getId());
        if (userTask.isEmpty())
            throw new NullPointerException("The user with idUser " + user.getId() + " does not have the task with idTask " + task.getId() + ".|method: removeTaskFromUser");
        userTaskRepository.deleteAll(userTask);
    }

    @Transactional
    public void removeAllTasksFromUser(User user) {
        List<UserTask> userTask = userTaskRepository.findByIdUser(user.getId());
        userTaskRepository.deleteAll(userTask);
    }


}
