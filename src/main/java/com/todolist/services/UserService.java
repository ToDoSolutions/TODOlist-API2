package com.todolist.services;

import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import com.todolist.dtos.Status;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.entity.UserTask;
import com.todolist.exceptions.BadRequestException;
import com.todolist.exceptions.NotFoundException;
import com.todolist.repositories.TaskRepository;
import com.todolist.repositories.UserRepository;
import com.todolist.repositories.UserTaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;


    private TaskService taskService;


    private UserTaskRepository userTaskRepository;

    @Transactional(readOnly = true)
    public List<User> findAllShowUsers(Sort sort) {
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
