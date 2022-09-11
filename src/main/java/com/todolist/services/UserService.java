package com.todolist.services;

import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
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


    private TaskRepository taskRepository;


    private UserTaskRepository userTaskRepository;


    public List<ShowUser> findAllShowUsers(Sort sort) {
        return userRepository.findAll(sort).stream().map(user -> new ShowUser(user, getShowTaskFromUser(user))).collect(Collectors.toList());
    }

    public User findUserById(Long idUser) {
        return userRepository.findById(idUser).orElse(null);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public List<User> findUsersWithTask(Task task) {
        return userRepository.findAll().stream().filter(user -> getTasksFromUser(user).contains(task)).toList();
    }

    public List<Task> getTasksFromUser(User user) {
        return userTaskRepository.findByIdUser(user.getIdUser()).stream()
                .map(userTask -> taskRepository.findById(userTask.getIdTask()).orElseThrow(() -> new NotFoundException("Task not found.")))
                .toList();
    }

    public List<ShowTask> getShowTaskFromUser(User user) {
        return getTasksFromUser(user).stream().map(ShowTask::new).toList();
    }

    public void addTaskToUser(User user, Task task) {
        userTaskRepository.save(new UserTask(user.getIdUser(), task.getIdTask()));
    }

    public void removeTaskFromUser(User user, Task task) {
        List<UserTask> userTask = userTaskRepository.findByIdTaskAndIdUser(task.getIdTask(), user.getIdUser());
        if (userTask.isEmpty())
            throw new NullPointerException("The user with idUser " + user.getIdUser() + " does not have the task with idTask " + task.getIdTask() + ".|method: removeTaskFromUser");
        userTaskRepository.deleteAll(userTask);
    }

    public void removeAllTasksFromUser(User user) {
        List<UserTask> userTask = userTaskRepository.findByIdUser(user.getIdUser());
        userTaskRepository.deleteAll(userTask);
    }

    public User updateUser(User oldUser, User user) {
        Validator validator;
        try {
            validator = Validation.buildDefaultValidatorFactory().getValidator();
        } catch (Exception e) {
            throw new BadRequestException("Error building validator.");
        }
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
        return saveUser(oldUser);
    }

}
