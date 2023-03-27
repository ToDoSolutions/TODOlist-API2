package com.todolist.services;

import com.todolist.component.DataManager;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.Status;
import com.todolist.dtos.autodoc.RoleStatus;
import com.todolist.entity.Role;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.exceptions.NotFoundException;
import com.todolist.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    // Repositories -----------------------------------------------------------
    private final UserRepository userRepository;


    // Components -------------------------------------------------------------
    private final DataManager dataManager;
    private final TaskService taskService;
    private final RoleService roleService;

    // Constructors -----------------------------------------------------------
    @Autowired
    public UserService(UserRepository userRepository, DataManager dataManager,
                       TaskService taskService, RoleService roleService) {
        this.userRepository = userRepository;
        this.dataManager = dataManager;
        this.taskService = taskService;
        this.roleService = roleService;
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
    public User findUserByIdClockify(String idClockify) {
        return userRepository.findByClockifyId(idClockify).stream().findFirst().orElseThrow(() -> new NotFoundException("The user with idUser " + idClockify + " does not exist."));
    }

    @Transactional(readOnly = true)
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("The user with username " + username + " does not exist."));
    }

    @Transactional
    public Long getTaskCompleted(User user) {
        return user.getTasks().stream().filter(task -> task.getStatus().equals(Status.DONE)).count();
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
        List<User> users = userRepository.findAll().stream().filter(user -> user.getTasks().contains(task)).toList();
        if (users.isEmpty())
            throw new NotFoundException("No users have the task with idTask " + task.getId() + ".");
        return users;
    }

    @Transactional(readOnly = true)
    public List<ShowTask> getShowTaskFromUser(User user) {
        return user.getTasks().stream().map(ShowTask::new).toList();
    }

    @Transactional(readOnly = true)
    public List<Task> getIndividualTask(User user) {
        return user.getTasks().stream().filter(task -> !task.getPosition().equals(0)).toList();
    }

    @Transactional(readOnly = true)
    public List<Task> getGroupTask(User user) {
        List<String> userTask = user.getTasks().stream().filter(task -> task.getPosition().equals(0)).map(Task::getTitle).toList();
        return taskService.findAllTasks().stream().filter(task -> task.getPosition().equals(0) && userTask.contains(task.getTitle())).toList();
    }

    // Save and delete --------------------------------------------------------
    @Transactional
    public void addTaskToUser(User user, Task task) {
        if (!user.getTasks().contains(task)) {
            task.setUser(user);
            taskService.saveTask(task);
            user.getTasks().add(task);
            saveUser(user);
        }
    }

    @Transactional
    public void removeTaskFromUser(User user, Task task) {
        List<Task> tasks = user.getTasks().stream().filter(task1 -> task1.getId().equals(task.getId())).toList();
        user.setTasks(tasks);
        saveUser(user);
    }

    @Transactional
    public void removeAllTasksFromUser(User user) {
        user.setTasks(null);
        saveUser(user);
    }

    /**
     * ROLES
     */
    // Finders ----------------------------------------------------------------
    @Transactional(readOnly = true)
    public Map<RoleStatus, Double> getCost(User user, String title) {
        Map<RoleStatus, Double> cost = new HashMap<>();
        for (Task task : user.getTasks())
            if (task.getTitle().contains(title)) {
                for (Role role : roleService.findRoleByTaskId(task.getId())) {
                    if (cost.containsKey(role.getStatus()))
                        cost.put(role.getStatus(), cost.get(role.getStatus()) + role.getSalary());
                    else
                        cost.put(role.getStatus(), role.getSalary());
                }
            }
        return cost;
    }

    @Transactional(readOnly = true)
    public Map<RoleStatus, Double> getTotalCost(User user) {
        return getCost(user, "");
    }

    @Transactional(readOnly = true)
    public Map<RoleStatus, Double> getIndividualCost(User user) {
        return getCost(user, "I");
    }

    @Transactional(readOnly = true)
    public Map<RoleStatus, Double> getGroupCost(User user) {
        Map<RoleStatus, Double> cost = new HashMap<>();
        List<Task> tasks = getGroupTask(user);
        for (Task task : tasks)
            for (Role role : roleService.findRoleByTaskId(task.getId())) {
                if (cost.containsKey(role.getStatus()))
                    cost.put(role.getStatus(), cost.get(role.getStatus()) + role.getSalary());
                else
                    cost.put(role.getStatus(), role.getSalary());
            }
        return cost;
    }
}
