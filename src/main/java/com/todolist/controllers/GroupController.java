package com.todolist.controllers;

import com.google.common.base.Preconditions;
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
                                                  @RequestParam(defaultValue = "-1") @Min(value = -1, message = "The limit must be positive") Integer limit,
                                                  @RequestParam(defaultValue = "idGroup") String order,
                                                  @RequestParam(defaultValue = ShowGroup.ALL_ATTRIBUTES) String fieldsGroup,
                                                  @RequestParam(defaultValue = ShowUser.ALL_ATTRIBUTES) String fieldsUser,
                                                  @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES) String fieldsTask,
                                                  @RequestParam(required = false) String name,
                                                  @RequestParam(required = false) String description,
                                                  @RequestParam(required = false) @Pattern(regexp = "[<>=]{2}\\d+|[<>=]\\d+", message = "The tasks' number is invalid.") String numTasks,
                                                  @RequestParam(required = false) @Pattern(regexp = "[<>=]{2}\\d{4}-\\d{2}-\\d{2}|[<>=]\\d{4}-\\d{2}-\\d{2}", message = "The createdDate is invalid.") String createdDate) {
        String propertyOrder = order.charAt(0) == '+' || order.charAt(0) == '-' ? order.substring(1) : order;
        Preconditions.checkArgument(Arrays.stream(ShowTask.ALL_ATTRIBUTES.split(",")).anyMatch(prop -> prop.equalsIgnoreCase(propertyOrder)), "The order is invalid.");
        Preconditions.checkArgument(Arrays.stream(fieldsGroup.split(",")).allMatch(field -> ShowTask.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase())), "The fields are invalid.");
        List<ShowGroup> result = new ArrayList<>(),
                groups = groupService.findAllShowGroups(Sort.by(order.charAt(0) == '-' ? Sort.Direction.DESC : Sort.Direction.ASC, propertyOrder));
        if (limit == -1) limit = groups.size() - 1;
        int start = offset == null || offset < 1 ? 0 : offset - 1; // Donde va a comenzar.
        int end = limit > groups.size() ? groups.size() : start + limit; // Donde va a terminar.
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
        Preconditions.checkNotNull(group, "The group with idGroup " + idGroup + " does not exist.");
        Preconditions.checkArgument(Arrays.stream(fieldsGroup.split(",")).allMatch(field -> ShowGroup.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase())), "The groups' fields are invalid.");
        Preconditions.checkArgument(Arrays.stream(fieldsUser.split(",")).allMatch(field -> ShowUser.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase())), "The users' fields are invalid.");
        Preconditions.checkArgument(Arrays.stream(fieldsTask.split(",")).allMatch(field -> ShowTask.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase())), "The tasks' fields are invalid.");
        return new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(fieldsGroup, fieldsUser, fieldsTask);
    }

    @PostMapping
    public Map<String, Object> addGroup(@RequestBody @Valid Group group) {
        Preconditions.checkNotNull(group, "The group with idGroup " + group.getIdGroup() + " must have name.");
        group = groupService.saveGroup(group);
        return new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @PutMapping
    public Map<String, Object> updateGroup(@RequestBody @Valid Group group) {
        Group oldGroup = groupService.findGroupById(group.getIdGroup());
        Preconditions.checkNotNull(oldGroup, "The group with idGroup " + group.getIdGroup() + " does not exist.");
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
        Preconditions.checkNotNull(group, "The group with idGroup " + idGroup + " does not exist.");
        groupService.deleteGroup(group);
        return new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @PostMapping("/{idGroup}/users/{idUser}")
    public Map<String, Object> addUserFromGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup,
                                                @PathVariable("idUser") @Min(value = 0, message = "The idUser must be positive.") Long idUser) {
        Group group = groupService.findGroupById(idGroup);
        Preconditions.checkNotNull(group, "The group with idGroup " + idGroup + " does not exist.");
        User user = userService.findUserById(idUser);
        Preconditions.checkNotNull(user, "The user with idUser " + idUser + " does not exist.");
        groupService.addUserToGroup(group, user);
        return new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @DeleteMapping("/{idGroup}/users/{idUser}")
    public Map<String, Object> deleteUserFromGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup,
                                                   @PathVariable("idUser") @Min(value = 0, message = "The idUser must be positive.") Long idUser) {
        Group group = groupService.findGroupById(idGroup);
        Preconditions.checkNotNull(group, "The group with idGroup " + idGroup + " does not exist.");
        User user = userService.findUserById(idUser);
        Preconditions.checkNotNull(user, "The user with idUser " + idUser + " does not exist.");
        groupService.removeUserFromGroup(group, user);
        return new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @PostMapping("/{idGroup}/tasks/{idTask}")
    public Map<String, Object> addTaskFromGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup,
                                                @PathVariable("idTask") @Min(value = 0, message = "The idTask must be positive.") Long idTask) {
        Group group = groupService.findGroupById(idGroup);
        Preconditions.checkNotNull(group, "The group with idGroup " + idGroup + " does not exist.");
        Task task = taskService.findTaskById(idTask);
        Preconditions.checkNotNull(task, "The task with idTask " + idTask + " does not exist.");
        groupService.addTaskToGroup(group, task);
        return new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @DeleteMapping("/{idGroup}/tasks/{idTask}")
    public Map<String, Object> deleteTaskFromGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup,
                                                   @PathVariable("idTask") @Min(value = 0, message = "The idTask must be positive.") Long idTask) {
        Group group = groupService.findGroupById(idGroup);
        Preconditions.checkNotNull(group, "The group with idGroup " + idGroup + " does not exist.");
        Task task = taskService.findTaskById(idTask);
        Preconditions.checkNotNull(task, "The task with idTask " + idTask + " does not exist.");
        groupService.removeTaskFromGroup(group, task);
        return new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @DeleteMapping("/{idGroup}/users")
    public Map<String, Object> deleteAllUsersFromGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup) {
        Group group = groupService.findGroupById(idGroup);
        Preconditions.checkNotNull(group, "The group with idGroup " + idGroup + " does not exist.");
        groupService.removeAllUsersFromGroup(group);
        return new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }
}
