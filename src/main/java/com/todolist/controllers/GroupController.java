package com.todolist.controllers;

import com.google.common.collect.Lists;
import com.todolist.dtos.ShowGroup;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import com.todolist.entity.Group;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.exceptions.BadRequestException;
import com.todolist.exceptions.NotFoundException;
import com.todolist.filters.DateFilter;
import com.todolist.filters.NumberFilter;
import com.todolist.services.GroupService;
import com.todolist.services.TaskService;
import com.todolist.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@Validated
@AllArgsConstructor
public class GroupController {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator(); // Arreglar algún día.

    private GroupService groupService;

    private TaskService taskService;

    private UserService userService;

    @GetMapping("/groups")
    public List<Map<String, Object>> getAllGroups(@RequestParam(defaultValue = "0") @Min(value = 0, message = "The offset must be positive.") Integer offset,
                                                  @RequestParam(defaultValue = "-1") @Min(value = -1, message = "The limit must be positive.") Integer limit,
                                                  @RequestParam(defaultValue = "idGroup") String order,
                                                  @RequestParam(defaultValue = ShowGroup.ALL_ATTRIBUTES) String fieldsGroup,
                                                  @RequestParam(defaultValue = ShowUser.ALL_ATTRIBUTES) String fieldsUser,
                                                  @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES) String fieldsTask,
                                                  @RequestParam(required = false) String name,
                                                  @RequestParam(required = false) String description,
                                                  @RequestParam(required = false) NumberFilter numTasks,
                                                  @RequestParam(required = false) DateFilter createdDate) {
        String propertyOrder = order.charAt(0) == '+' || order.charAt(0) == '-' ? order.substring(1) : order;
        List<String> fieldsGroupList = List.of(ShowGroup.ALL_ATTRIBUTES.toLowerCase().split(","));
        List<String> listUserFields = List.of(ShowUser.ALL_ATTRIBUTES.toLowerCase().split(","));
        List<String> listTaskFields = List.of(ShowTask.ALL_ATTRIBUTES.toLowerCase().split(","));
        if (fieldsGroupList.stream().noneMatch(prop -> prop.equalsIgnoreCase(propertyOrder)))
            throw  new BadRequestException("The order is invalid.");
        if (!Arrays.stream(fieldsGroup.split(",")).allMatch(field -> fieldsGroupList.contains(field.toLowerCase())))
            throw new BadRequestException("The groups' fields are invalid.");
        if (!Arrays.stream(fieldsUser.split(",")).allMatch(field -> listUserFields.contains(field.toLowerCase())))
            throw new BadRequestException("The users' fields are invalid.");
        if (!Arrays.stream(fieldsTask.split(",")).allMatch(field -> listTaskFields.contains(field.toLowerCase())))
            throw new BadRequestException("The tasks' fields are invalid.");
        List<ShowGroup> result = Lists.newArrayList(),
                groups = groupService.findAllShowGroups(Sort.by(order.charAt(0) == '-' ? Sort.Direction.DESC : Sort.Direction.ASC, propertyOrder));
        if (limit == -1) limit = groups.size();
        int start = offset == null || offset < 1 ? 0 : offset - 1; // Donde va a comenzar.
        int end = limit > groups.size() || start + limit > groups.size() ? limit : start + limit; // Donde va a terminar.
        for (int i = start; i < end; i++) {
            ShowGroup group = groups.get(i);
            if (group != null &&
                    (name == null || group.getName().equals(name)) &&
                    (description == null || group.getDescription().equals(description)) &&
                    (numTasks == null || numTasks.isValid(group.getNumTasks())) &&
                    (createdDate == null || createdDate.isValid(group.getCreatedDate())))
                result.add(group);
        }
        return result.stream().map(group -> group.getFields(fieldsGroup, fieldsUser, fieldsTask)).collect(Collectors.toList());
    }

    @GetMapping("/group/{idGroup}")
    public Map<String, Object> getGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup,
                                        @RequestParam(defaultValue = ShowGroup.ALL_ATTRIBUTES) String fieldsGroup,
                                        @RequestParam(defaultValue = ShowUser.ALL_ATTRIBUTES) String fieldsUser,
                                        @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES) String fieldsTask) {
        Group group = groupService.findGroupById(idGroup);
        if (group == null) throw new NotFoundException("The group with idGroup " + idGroup + " does not exist.");
        if (!Arrays.stream(fieldsGroup.split(",")).allMatch(field -> ShowGroup.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase())))
            throw new BadRequestException("The groups' fields are invalid.");
        if (!Arrays.stream(fieldsUser.split(",")).allMatch(field -> ShowUser.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase())))
            throw new BadRequestException("The users' fields are invalid.");
        if (!Arrays.stream(fieldsTask.split(",")).allMatch(field -> ShowTask.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase())))
            throw new BadRequestException("The tasks' fields are invalid.");
        return new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(fieldsGroup, fieldsUser, fieldsTask);
    }

    @PostMapping("/group")
    public Map<String, Object> addGroup(@RequestBody @Valid Group group) {
        if (group.getName() == null || group.getName().isEmpty())
            throw new BadRequestException("The group with idGroup " + group.getIdGroup() + " must have name.");
        if (group.getCreatedDate() == null) group.setCreatedDate(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        group = groupService.saveGroup(group);
        return new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @PutMapping("/group")
    public Map<String, Object> updateGroup(@RequestBody @Valid Group group) {
        Group oldGroup = groupService.findGroupById(group.getIdGroup());
        if (oldGroup == null) throw new BadRequestException("The group with idGroup " + group.getIdGroup() + " does not exist.");
        if (group.getName() != null && !Objects.equals(group.getName(), ""))
            oldGroup.setName(group.getName());
        if (group.getDescription() != null && !Objects.equals(group.getDescription(), ""))
            oldGroup.setDescription(group.getDescription());
        if (group.getCreatedDate() != null && !Objects.equals(group.getCreatedDate(), ""))
            oldGroup.setCreatedDate(group.getCreatedDate());
        Set<ConstraintViolation<Group>> errors = validator.validate(oldGroup);
        if (!errors.isEmpty())
            throw new ConstraintViolationException(errors);
        oldGroup = groupService.saveGroup(oldGroup);
        return new ShowGroup(oldGroup, groupService.getShowUserFromGroup(oldGroup)).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @DeleteMapping("/group/{idGroup}")
    public Map<String, Object> deleteGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup) {
        Group group = groupService.findGroupById(idGroup);
        if (group == null) throw new NotFoundException("The group with idGroup " + idGroup + " does not exist.");
        groupService.deleteGroup(group);
        return new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @SuppressWarnings("unchecked")
    @PutMapping("/group/{idGroup}/user/{idUser}")
    public Map<String, Object> addUserToGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup,
                                              @PathVariable("idUser") @Min(value = 0, message = "The idUser must be positive.") Long idUser) {
        Group group = groupService.findGroupById(idGroup);
        if (group == null) throw new NotFoundException("The group with idGroup " + idGroup + " does not exist.");
        User user = userService.findUserById(idUser);
        if (user == null) throw new NotFoundException("The user with idUser " + idUser + " does not exist.");
        if (!groupService.getShowUserFromGroup(group).contains(new ShowUser(user, userService.getShowTaskFromUser(user))))
            groupService.addUserToGroup(group, user);
        return new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @SuppressWarnings("unchecked")
    @DeleteMapping("/group/{idGroup}/user/{idUser}")
    public Map<String, Object> deleteUserFromGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup,
                                                   @PathVariable("idUser") @Min(value = 0, message = "The idUser must be positive.") Long idUser) {
        Group group = groupService.findGroupById(idGroup);
        if (group == null) throw new NotFoundException("The group with idGroup " + idGroup + " does not exist.");
        User user = userService.findUserById(idUser);
        if (user == null) throw new NotFoundException("The user with idUser " + idUser + " does not exist.");
        groupService.removeUserFromGroup(group, user);
        return new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @SuppressWarnings("unchecked")
    @PutMapping("/group/{idGroup}/task/{idTask}")
    public Map<String, Object> addTaskToGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup,
                                                @PathVariable("idTask") @Min(value = 0, message = "The idTask must be positive.") Long idTask) {
        Group group = groupService.findGroupById(idGroup);
        if (group == null) throw new NotFoundException("The group with idGroup " + idGroup + " does not exist.");
        Task task = taskService.findTaskById(idTask);
        if (task == null) throw new NotFoundException("The task with idTask " + idTask + " does not exist.");
        if (groupService.getShowUserFromGroup(group).stream().noneMatch(user -> user.getTasks().contains(new ShowTask(task))))
            groupService.addTaskToGroup(group, task);
        return new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @SuppressWarnings("unchecked")
    @DeleteMapping("group/{idGroup}/task/{idTask}")
    public Map<String, Object> deleteTaskFromGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup,
                                                   @PathVariable("idTask") @Min(value = 0, message = "The idTask must be positive.") Long idTask) {
        Group group = groupService.findGroupById(idGroup);
        if (group == null) throw new NotFoundException("The group with idGroup " + idGroup + " does not exist.");
        Task task = taskService.findTaskById(idTask);
        if (task == null) throw new NotFoundException("The task with idTask " + idTask + " does not exist.");
        if (groupService.getShowUserFromGroup(group).stream().anyMatch(user -> user.getTasks().contains(new ShowTask(task))))
            groupService.removeTaskFromGroup(group, task);
        return new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @DeleteMapping("/group/{idGroup}/users")
    public Map<String, Object> deleteAllUsersFromGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup) {
        Group group = groupService.findGroupById(idGroup);
        if (group == null) throw new NotFoundException("The group with idGroup " + idGroup + " does not exist.");
        groupService.removeAllUsersFromGroup(group);
        return new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/groups/user/{idUser}")
    public List<Map<String, Object>> getGroupsWithUser(@PathVariable("idUser") @Min(value = 0, message = "The idUser must be positive.") Long idUser) {
        User user = userService.findUserById(idUser);
        if (user == null) throw new NotFoundException("The user with idUser " + idUser + " does not exist.");
        List<Group> groups = groupService.findGroupsWithUser(user);
        if (groups == null || groups.isEmpty())
            throw new BadRequestException("The user with idUser " + idUser + " does not belong to any group.");
        return groups.stream().map(group -> new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES)).toList();
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/groups/task/{idTask}")
    public List<Map<String, Object>> getGroupsWithTask(@PathVariable("idTask") @Min(value = 0, message = "The idTask must be positive.") Long idTask) {
        Task task = taskService.findTaskById(idTask);
        if (task == null) throw new NotFoundException("The task with idTask " + idTask + " does not exist.");
        List<Group> groups = groupService.findGroupsWithTask(task);
        if (groups == null || groups.isEmpty())
            throw new BadRequestException("The task with idTask " + idTask + " does not belong to any group.");
        return groups.stream().map(group -> new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES)).toList();
    }
}
