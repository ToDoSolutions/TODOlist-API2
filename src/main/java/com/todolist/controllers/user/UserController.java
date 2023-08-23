package com.todolist.controllers.user;

import com.fadda.common.Preconditions;
import com.todolist.component.DTOManager;
import com.todolist.dtos.Order;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import com.todolist.entity.User;
import com.todolist.exceptions.BadRequestException;
import com.todolist.filters.NumberFilter;
import com.todolist.services.user.UserService;
import com.todolist.services.user.UserTaskService;
import com.todolist.validators.FieldValidator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    // Services ---------------------------------------------------------------

    private final UserService userService;
    private final UserTaskService userTaskService;

    // Components -------------------------------------------------------------
    private final DTOManager dtoManager;

    // Validators -------------------------------------------------------------
    private final Consumer<String[]> fieldValidator;

    // Constructors -----------------------------------------------------------
    @Autowired
    public UserController(UserService userService, UserTaskService userTaskService, FieldValidator fieldValidator, DTOManager dtoManager) {
        this.userService = userService;
        this.userTaskService = userTaskService;
        this.fieldValidator = fields -> {
            fieldValidator.taskFieldValidate(fields[0]);
            fieldValidator.userFieldValidate(fields[1]);
        };
        this.dtoManager = dtoManager;
    }

    /* ------------ */
    // CRUD Methods //
    /* ------------ */

    // Getters -----------------------------------------------------------------
    @GetMapping("/users")
    public List<Map<String, Object>> getAllUsers(@RequestParam(defaultValue = "0") @Min(value = 0, message = "The offset must be positive.") Integer offset,
                                                 @RequestParam(defaultValue = Integer.MAX_VALUE + "") @Min(value = 0, message = "The limit must be positive.") Integer limit,
                                                 @RequestParam(defaultValue = "+id") Order order,
                                                 @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES_STRING) String fieldsTask,
                                                 @RequestParam(defaultValue = ShowUser.ALL_ATTRIBUTES_STRING) String fieldsUser,
                                                 @RequestParam(required = false) String name,
                                                 @RequestParam(required = false) String surname,
                                                 @RequestParam(required = false) @Email(message = "The email is invalid.") String email,
                                                 @RequestParam(required = false) @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z\\d+&@#/%?=~_|!:,.;]*[-a-zA-Z\\d+&@#/%=~_|]", message = "The avatar is invalid.") String avatar,
                                                 @RequestParam(required = false) String bio,
                                                 @RequestParam(required = false) String location,
                                                 @RequestParam(required = false) NumberFilter taskCompleted) {
        order.validateOrder(fieldsUser);
        List<User> users = userService.findAllUsers(order.getSort());
        Stream<User> result = users.stream().skip(offset);
        if (limit != -1)
            result = result.limit(limit);
        return result.filter(user -> Objects.nonNull(user) &&
                        Preconditions.isNullOrValid(name, n -> user.getName().equals(n)) &&
                        Preconditions.isNullOrValid(surname, s -> user.getSurname().equals(s)) &&
                        Preconditions.isNullOrValid(email, e -> user.getEmail().equals(e)) &&
                        Preconditions.isNullOrValid(location, l -> user.getLocation().equals(l)) &&
                        Preconditions.isNullOrValid(taskCompleted, t -> t.isValid(userService.getTaskCompleted(user))) &&
                        Preconditions.isNullOrValid(bio, b -> user.getBio().contains(b)) &&
                        Preconditions.isNullOrValid(avatar, a -> user.getAvatar().equals(a)))
                .map(user -> new ShowUser(user, userTaskService.getShowTasksFromUser(user)))
                .map(user -> dtoManager.getEntityAsJson(user, fieldValidator, fieldsTask, fieldsUser)).toList();
    }

    @GetMapping("/user/{idUser}")
    public Map<String, Object> getUser(@PathVariable("idUser") @Min(value = 0, message = "The idUser must be positive.") Integer idUser,
                                       @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES_STRING) String fieldsTask,
                                       @RequestParam(defaultValue = ShowUser.ALL_ATTRIBUTES_STRING) String fieldsUser) {
        User user = userService.findUserById(idUser);
        ShowUser showUser = new ShowUser(user, userTaskService.getShowTasksFromUser(user));
        return dtoManager.getEntityAsJson(showUser, fieldValidator, fieldsTask, fieldsUser);
    }

    // Adders ------------------------------------------------------------------
    @PostMapping("/user")
    public ResponseEntity<ShowUser> addUser(@RequestBody @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new BadRequestException(bindingResult.getAllErrors().get(0).getDefaultMessage());
        user = userService.saveUser(user);
        ShowUser showUser = new ShowUser(user, userTaskService.getShowTasksFromUser(user));
        return ResponseEntity.ok(showUser);
    }

    // Updaters ----------------------------------------------------------------
    @PutMapping("/user")
    public ResponseEntity<ShowUser> updateUser(@RequestBody @Valid User user, BindingResult bindingResult,
                                                @RequestHeader("Password") String password) {
        if (bindingResult.hasErrors())
            throw new BadRequestException(bindingResult.getAllErrors().get(0).getDefaultMessage());
        User oldUser = userService.findUserById(user.getId());
        if (password == null || password.isEmpty())
            throw new BadRequestException("The password is required.");
        if (!oldUser.getPassword().equals(password))
            throw new BadRequestException("The password is incorrect.");
        BeanUtils.copyProperties(user, oldUser, "idUser", "password", "token", "tasks");
        userService.saveUser(oldUser);
        ShowUser showUser = new ShowUser(user, userTaskService.getShowTasksFromUser(user));
        return ResponseEntity.ok(showUser);
    }

    // Deleters ----------------------------------------------------------------
    @DeleteMapping("/user/{idUser}")
    public Map<String, Object> deleteUser(@PathVariable("idUser") Integer idUser,
                                          @RequestHeader("Password") String password) {
        User user = userService.findUserById(idUser);
        if (password == null || password.isEmpty())
            throw new BadRequestException("The password is required.");
        if (!Objects.equals(user.getPassword(), password))
            throw new BadRequestException("The password is incorrect.");
        userService.deleteUser(user);
        ShowUser showUser = new ShowUser(user, userTaskService.getShowTasksFromUser(user));
        return showUser.toJson();
    }
}
