package com.todolist.controllers;

import com.google.common.collect.Lists;
import com.todolist.component.DTOManager;
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
import com.todolist.entity.IterableEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;
import javax.validation.constraints.Min;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class GroupController {

    private final GroupService groupService;

    private final TaskService taskService;

    private final UserService userService;
    private final DTOManager dtoManager;


    @Autowired
    public GroupController(GroupService groupService, TaskService taskService, UserService userService, DTOManager dtoManager) {
        this.groupService = groupService;
        this.taskService = taskService;
        this.userService = userService;
        this.dtoManager = dtoManager;
    }
    /* GROUP OPERATIONS */

    @DeleteMapping("/group/{idGroup}") // DeleteTest
    public Map<String, Object> deleteGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup) {
        Group group = groupService.findGroupById(idGroup);
        if (group == null) throw new NotFoundException("The group with idGroup " + idGroup + " does not exist.");
        groupService.deleteGroup(group);
        return new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @GetMapping("/groups") // GetAllTest
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
        // Extraer en un validador.
        if (fieldsGroupList.stream().noneMatch(prop -> prop.equalsIgnoreCase(propertyOrder)))
            throw new BadRequestException("The order is invalid.");
        //
        List<Group> result = Lists.newArrayList(),
                groups = groupService.findAllGroups(Sort.by(order.charAt(0) == '-' ? Sort.Direction.DESC : Sort.Direction.ASC, propertyOrder));
        IterableEntity<Group> iterableEntity = new IterableEntity(groups, limit, offset);
        for (Group group : iterableEntity) {
            if (group != null &&
                    (name == null || group.getName().equals(name)) &&
                    (description == null || group.getDescription().equals(description)) &&
                    (numTasks == null || numTasks.isValid(groupService.getNumTasks(group))) &&
                    (createdDate == null || createdDate.isValid(group.getCreatedDate())))
                result.add(group);
        }
        return result.stream().map(group -> dtoManager.getShowGroupAsJson(group, fieldsGroup, fieldsUser, fieldsTask)).toList();
    }

    @GetMapping("/group/{idGroup}") // GetSoloTest
    public Map<String, Object> getGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup,
                                        @RequestParam(defaultValue = ShowGroup.ALL_ATTRIBUTES) String fieldsGroup,
                                        @RequestParam(defaultValue = ShowUser.ALL_ATTRIBUTES) String fieldsUser,
                                        @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES) String fieldsTask) {
        Group group = groupService.findGroupById(idGroup);
        return dtoManager.getShowGroupAsJson(group, fieldsGroup, fieldsUser, fieldsTask);
    }

    @PostMapping("/group") // PostTest
    public Map<String, Object> addGroup(@RequestBody @Valid Group group) {
        group = groupService.saveGroup(group);
        return new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @PutMapping("/group") // PutTest
    public Map<String, Object> updateGroup(@RequestBody @Valid Group group, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new BadRequestException("The group with idGroup " + group.getIdGroup() + " is invalid.");
        Group oldGroup = groupService.findGroupById(group.getIdGroup());
        BeanUtils.copyProperties(group, oldGroup, "idGroup", "createdDate");
        oldGroup = groupService.saveGroup(oldGroup);
        return dtoManager.getShowGroupAsJson(oldGroup);
    }

    /* USER OPERATIONS */

    @DeleteMapping("/group/{idGroup}/users") // DeleteAllTest
    public Map<String, Object> deleteAllUsersFromGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup) {
        Group group = groupService.findGroupById(idGroup);
        groupService.removeAllUsersFromGroup(group);
        return dtoManager.getShowGroupAsJson(group);
    }


    @DeleteMapping("/group/{idGroup}/user/{idUser}") // DeleteTest
    public Map<String, Object> deleteUserFromGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup,
                                                   @PathVariable("idUser") @Min(value = 0, message = "The idUser must be positive.") Long idUser) {
        Group group = groupService.findGroupById(idGroup);
        User user = userService.findUserById(idUser);
        groupService.removeUserFromGroup(group, user);
        return dtoManager.getShowGroupAsJson(group);
    }


    @GetMapping("/groups/user/{idUser}") // GetAllTest
    public List<Map<String, Object>> getGroupsWithUser(@PathVariable("idUser") @Min(value = 0, message = "The idUser must be positive.") Long idUser) {
        User user = userService.findUserById(idUser);
        List<Group> groups = groupService.findGroupsWithUser(user);
        return groups.stream().map(dtoManager::getShowGroupAsJson).toList();
    }


    @PutMapping("/group/{idGroup}/user/{idUser}") // PutTest
    public Map<String, Object> addUserToGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup,
                                              @PathVariable("idUser") @Min(value = 0, message = "The idUser must be positive.") Long idUser) {
        Group group = groupService.findGroupById(idGroup);
        User user = userService.findUserById(idUser);
        if (!groupService.getShowUserFromGroup(group).contains(new ShowUser(user, userService.getShowTaskFromUser(user))))
            groupService.addUserToGroup(group, user);
        return dtoManager.getShowGroupAsJson(group);
    }

    /* TASK OPERATIONS */

    @DeleteMapping("/group/{idGroup}/tasks") // DeleteAllTest
    public Map<String, Object> deleteAllTasksFromGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup) {
        Group group = groupService.findGroupById(idGroup);
        groupService.removeAllTasksFromGroup(group);
        return dtoManager.getShowGroupAsJson(group);
    }


    @DeleteMapping("group/{idGroup}/task/{idTask}") // DeleteTest
    public Map<String, Object> deleteTaskFromGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup,
                                                   @PathVariable("idTask") @Min(value = 0, message = "The idTask must be positive.") Long idTask) {
        Group group = groupService.findGroupById(idGroup);
        Task task = taskService.findTaskById(idTask);
        if (groupService.getShowUserFromGroup(group).stream().anyMatch(user -> user.getTasks().contains(new ShowTask(task))))
            groupService.removeTaskFromGroup(group, task);
        return dtoManager.getShowGroupAsJson(group);
    }

    @GetMapping("/groups/task/{idTask}") // GetAllTest
    public List<Map<String, Object>> getGroupsWithTask(@PathVariable("idTask") @Min(value = 0, message = "The idTask must be positive.") Long idTask) {
        Task task = taskService.findTaskById(idTask);
        List<Group> groups = groupService.findGroupsWithTask(task);
        return groups.stream().map(dtoManager::getShowGroupAsJson).toList();
    }

    @PutMapping("/group/{idGroup}/task/{idTask}") // PutTest
    public Map<String, Object> addTaskToGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup,
                                              @PathVariable("idTask") @Min(value = 0, message = "The idTask must be positive.") Long idTask) {
        Group group = groupService.findGroupById(idGroup);
        Task task = taskService.findTaskById(idTask);
        if (groupService.getShowUserFromGroup(group).stream().noneMatch(user -> user.getTasks().contains(new ShowTask(task))))
            groupService.addTaskToGroup(group, task);
        return dtoManager.getShowGroupAsJson(group);
    }
}
