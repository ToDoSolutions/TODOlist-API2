package com.todolist.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todolist.component.DataManager;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.Status;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.entity.UserTask;
import com.todolist.exceptions.NotFoundException;
import com.todolist.repositories.UserRepository;
import com.todolist.repositories.UserTaskRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;


    private final TaskService taskService;


    private final UserTaskRepository userTaskRepository;
    private final DataManager dataManager;

    public UserService(UserRepository userRepository, TaskService taskService, UserTaskRepository userTaskRepository, DataManager dataManager) {
        this.userRepository = userRepository;
        this.taskService = taskService;
        this.userTaskRepository = userTaskRepository;
        this.dataManager = dataManager;
    }

    @PostConstruct
    @Transactional
    public void init() throws IOException {
        loadData();
    }

    @Transactional
    public void loadData() throws IOException {
        List<User> users = dataManager.loadUser();
        userRepository.saveAll(users);
        List<UserTask> userTasks = dataManager.loadUserTask();
        userTaskRepository.saveAll(userTasks);
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers(Sort sort) {
        return userRepository.findAll(sort);
    }

    @Transactional(readOnly = true)
    public User findUserById(Long idUser) {
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

    @Transactional
    public Task findTaskByTitle(String username, String title) {
        User user = findUserByUsername(username);
        return getTasksFromUser(user).stream().filter(task -> task.getTitle().equals(title)).findFirst().orElseThrow(() -> new NotFoundException("The task with title " + title + " does not exist."));
    }

    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public List<User> findUsersWithTask(Task task) {
        List<User> users = userRepository.findAll().stream().filter(user -> getTasksFromUser(user).contains(task)).toList();
        if (users.isEmpty())
            throw new NotFoundException("No users have the task with idTask " + task.getIdTask() + ".");
        return users;
    }

    @Transactional(readOnly = true)
    public List<Task> getTasksFromUser(User user) {
        return userTaskRepository.findByIdUser(user.getIdUser()).stream()
                .map(userTask -> taskService.findTaskById(userTask.getIdTask()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ShowTask> getShowTaskFromUser(User user) {
        return getTasksFromUser(user).stream().map(ShowTask::new).toList();
    }

    @Transactional
    public void addTaskToUser(User user, Task task) {
        userTaskRepository.save(new UserTask(user.getIdUser(), task.getIdTask()));
    }

    @Transactional
    public void removeTaskFromUser(User user, Task task) {
        List<UserTask> userTask = userTaskRepository.findByIdTaskAndIdUser(task.getIdTask(), user.getIdUser());
        if (userTask.isEmpty())
            throw new NullPointerException("The user with idUser " + user.getIdUser() + " does not have the task with idTask " + task.getIdTask() + ".|method: removeTaskFromUser");
        userTaskRepository.deleteAll(userTask);
    }

    @Transactional
    public void removeAllTasksFromUser(User user) {
        List<UserTask> userTask = userTaskRepository.findByIdUser(user.getIdUser());
        userTaskRepository.deleteAll(userTask);
    }


}
