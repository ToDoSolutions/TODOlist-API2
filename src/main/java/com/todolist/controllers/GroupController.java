package com.todolist.controllers;

import com.todolist.dtos.ShowGroup;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import com.todolist.entity.Group;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.services.GroupService;
import com.todolist.services.TaskService;
import com.todolist.services.UserService;
import com.todolist.utilities.Filter;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/groups")
@Validated
@AllArgsConstructor
public class GroupController {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator(); // Arreglar algún día.

    private GroupService groupService;

    private TaskService taskService;

    private UserService userService;

    @GetMapping
    public List<Map<String, Object>> getAllGroups(@RequestParam(defaultValue = "0") @Min(value = 0, message = "The offset must be positive.") Integer offset,
                                                  @RequestParam(defaultValue = Integer.MAX_VALUE + "") @Min(value = 0, message = "The limit must be positive") Integer limit,
                                                  @RequestParam(defaultValue = "idGroup") String order,
                                                  @RequestParam(defaultValue = ShowGroup.ALL_ATTRIBUTES) String fieldsGroup,
                                                  @RequestParam(defaultValue = ShowUser.ALL_ATTRIBUTES) String fieldsUser,
                                                  @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES) String fieldsTask,
                                                  @RequestParam(required = false) String name,
                                                  @RequestParam(required = false) String description,
                                                  @RequestParam(required = false) @Pattern(regexp = "[<>=]{2}\\d+|[<>=]\\d+", message = "The tasks' number is invalid.") String numTasks,
                                                  @RequestParam(required = false) @Pattern(regexp = "[<>=]{2}\\d{4}-\\d{2}-\\d{2}|[<>=]\\d{4}-\\d{2}-\\d{2}", message = "The createdDate is invalid.") String createdDate) {
        List<ShowGroup> result = new ArrayList<>(),
                groups = groupService.findAllShowGroups(Sort.by(order.charAt(0) == '-' ? Sort.Direction.DESC : Sort.Direction.ASC, order.charAt(0) == '+' || order.charAt(0) == '-' ? order.substring(1, order.length() - 1) : order));
        int start = offset == null || offset < 1 ? 0 : offset - 1; // Donde va a comenzar.
        int end = limit == null || limit > groups.size() ? groups.size() : start + limit; // Donde va a terminar.
        for (int i = start; i < end; i++) {
            ShowGroup group = groups.get(i);
            if (group != null &&
                    (name == null || group.getName().equals(name)) &&
                    (description == null || group.getDescription().equals(description)) &&
                    (numTasks == null || Filter.isGEL((long) group.getNumTasks(), numTasks)) &&
                    (createdDate == null || Filter.isGEL(group.getCreatedDate(), createdDate)))
                result.add(group);
        }
        return result.stream().map(group -> group.getFields(fieldsGroup, fieldsUser, fieldsTask)).collect(Collectors.toList());
    }

    @GetMapping("/{idGroup}")
    public Map<String, Object> getGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup,
                                        @RequestParam(defaultValue = ShowGroup.ALL_ATTRIBUTES) String fieldsGroup,
                                        @RequestParam(defaultValue = ShowUser.ALL_ATTRIBUTES) String fieldsUser,
                                        @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES) String fieldsTask) {
        Group group = groupService.findGroupById(idGroup);
        if (group == null)
            throw new NullPointerException("The group with idGroup " + idGroup + " does not exist.|/api/v1/groups/" + idGroup);
        if (!Arrays.stream(fieldsGroup.split(",")).allMatch(field -> ShowGroup.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase())))
            throw new IllegalArgumentException("The groups' fields are invalid.|/api/v1/groups/" + idGroup);
        if (!Arrays.stream(fieldsUser.split(",")).allMatch(field -> ShowUser.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase())))
            throw new IllegalArgumentException("The users' fields are invalid.|/api/v1/groups/" + idGroup);
        if (!Arrays.stream(fieldsTask.split(",")).allMatch(field -> ShowTask.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase())))
            throw new IllegalArgumentException("The tasks' fields are invalid.|/api/v1/groups/" + idGroup);
        return new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(fieldsGroup, fieldsUser, fieldsTask);
    }

    @PostMapping
    public Map<String, Object> addGroup(@RequestBody @Valid Group group) {
        if (group.getName() == null)
            throw new IllegalArgumentException("The group with idGroup " + group.getIdGroup() + " must have name.|/api/v1/groups/");
        groupService.saveGroup(group);
        return new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @PutMapping
    public Map<String, Object> updateGroup(@RequestBody @Valid Group group) {
        Group oldGroup = groupService.findGroupById(group.getIdGroup());
        if (oldGroup == null)
            throw new NullPointerException("The group with idGroup " + group.getIdGroup() + " does not exist.|/api/v1/groups/" + group.getIdGroup());
        if (group.getName() != null)
            oldGroup.setName(group.getName());
        if (group.getDescription() != null)
            oldGroup.setDescription(group.getDescription());
        if (group.getCreatedDate() != null)
            oldGroup.setCreatedDate(group.getCreatedDate());
        Set<ConstraintViolation<Group>> errors = validator.validate(oldGroup);
        if (!errors.isEmpty())
            throw new ConstraintViolationException(errors);
        oldGroup = groupService.saveGroup(oldGroup);
        return new ShowGroup(oldGroup, groupService.getShowUserFromGroup(oldGroup)).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @DeleteMapping("/{idGroup}")
    public Map<String, Object> deleteGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup) {
        Group group = groupService.findGroupById(idGroup);
        if (group == null)
            throw new NullPointerException("The group with idGroup " + idGroup + " does not exist.|/api/v1/groups/" + idGroup);
        groupService.deleteGroup(group);
        return new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @PostMapping("/{idGroup}/users/{idUser}")
    public Map<String, Object> addUserFromGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup,
                                                @PathVariable("idUser") @Min(value = 0, message = "The idUser must be positive.") Long idUser) {
        Group group = groupService.findGroupById(idGroup);
        if (group == null)
            throw new NullPointerException("The group with idGroup " + idGroup + " does not exist.|/api/v1/groups/" + idGroup);
        User user = userService.findUserById(idUser);
        if (user == null)
            throw new NullPointerException("The user with idUser " + idUser + " does not exist.|/api/v1/users/" + idUser);
        groupService.addUserToGroup(group, user);
        return new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @DeleteMapping("/{idGroup}/users/{idUser}")
    public Map<String, Object> deleteUserFromGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup,
                                                   @PathVariable("idUser") @Min(value = 0, message = "The idUser must be positive.") Long idUser) {
        Group group = groupService.findGroupById(idGroup);
        if (group == null)
            throw new NullPointerException("The group with idGroup " + idGroup + " does not exist.|/api/v1/groups/" + idGroup);
        User user = userService.findUserById(idUser);
        if (user == null)
            throw new NullPointerException("The user with idUser " + idUser + " does not exist.|/api/v1/users/" + idUser);
        groupService.removeUserFromGroup(group, user);
        return new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @PostMapping("/{idGroup}/tasks/{idTask}")
    public Map<String, Object> addTaskFromGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup,
                                                @PathVariable("idTask") @Min(value = 0, message = "The idTask must be positive.") Long idTask) {
        Group group = groupService.findGroupById(idGroup);
        if (group == null)
            throw new NullPointerException("The group with idGroup " + idGroup + " does not exist.|/api/v1/groups/" + idGroup);
        Task task = taskService.findTaskById(idTask);
        if (task == null)
            throw new NullPointerException("The task with idTask " + idTask + " does not exist.|/api/v1/tasks/" + idTask);
        groupService.addTaskToGroup(group, task);
        return new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @DeleteMapping("/{idGroup}/tasks/{idTask}")
    public Map<String, Object> deleteTaskFromGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup,
                                                   @PathVariable("idTask") @Min(value = 0, message = "The idTask must be positive.") Long idTask) {
        Group group = groupService.findGroupById(idGroup);
        if (group == null)
            throw new NullPointerException("The group with idGroup " + idGroup + " does not exist.|/api/v1/groups/" + idGroup);
        Task task = taskService.findTaskById(idTask);
        if (task == null)
            throw new NullPointerException("The task with idTask " + idTask + " does not exist.|/api/v1/tasks/" + idTask);
        groupService.removeTaskFromGroup(group, task);
        return new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @DeleteMapping("/{idGroup}/users")
    public Map<String, Object> deleteAllUsersFromGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup) {
        Group group = groupService.findGroupById(idGroup);
        if (group == null)
            throw new NullPointerException("The group with idGroup " + idGroup + " does not exist.|/api/v1/groups/" + idGroup);
        groupService.removeAllUsersFromGroup(group);
        return new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }
}
