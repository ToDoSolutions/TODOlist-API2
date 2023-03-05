package com.todolist.services;

import com.todolist.dtos.ShowGroup;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import com.todolist.entity.*;
import com.todolist.exceptions.BadRequestException;
import com.todolist.exceptions.NotFoundException;
import com.todolist.repositories.GroupRepository;
import com.todolist.repositories.GroupUserRepository;
import com.todolist.repositories.UserTaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GroupService {


    private GroupRepository groupRepository;


    private UserTaskRepository userTaskRepository;


    private GroupUserRepository groupUserRepository;


    private UserService userService;

    @Transactional(readOnly = true)
    public List<Group> findAllGroups(Sort sort) {
        return groupRepository.findAll(sort);
    }

    @Transactional(readOnly = true)
    public Group findGroupById(Long idGroup) {
        return groupRepository.findById(idGroup).orElseThrow(() -> new NotFoundException("The group with idGroup " + idGroup + " does not exist."));
    }

    @Transactional(readOnly = true)
    public Long getNumTasks(Group group) {
        return (long) getUsersFromGroup(group).stream()
                .flatMap(user -> userService.getTasksFromUser(user).stream())
                .collect(Collectors.toSet()).size();
    }

    @Transactional
    public Group saveGroup(Group group) {
        if (group.getCreatedDate() == null) group.setCreatedDate(LocalDate.now());
        return groupRepository.save(group);
    }

    @Transactional
    public void deleteGroup(Group group) {
        groupRepository.delete(group);
    }

    @Transactional
    public List<User> getUsersFromGroup(Group group) {
        return groupUserRepository.findByIdGroup(group.getIdGroup()).stream()
                .map(groupUser -> userService.findUserById(groupUser.getIdUser()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ShowUser> getShowUserFromGroup(Group group) {
        return getUsersFromGroup(group).stream().map(user -> new ShowUser(user, userService.getShowTaskFromUser(user))).toList();
    }

    @Transactional
    public void addUserToGroup(Group group, User user) {
        groupUserRepository.save(new GroupUser(group.getIdGroup(), user.getIdUser()));
    }

    @Transactional
    public void removeUserFromGroup(Group group, User user) {
        List<GroupUser> groupUser = groupUserRepository.findByIdGroupAndIdUser(group.getIdGroup(), user.getIdUser());
        groupUserRepository.deleteAll(groupUser);
    }

    @Transactional
    public void removeAllUsersFromGroup(Group group) {
        List<GroupUser> groupUser = groupUserRepository.findByIdGroup(group.getIdGroup());
        if (groupUser == null)
            throw new NotFoundException("The group with idGroup " + group.getIdGroup() + " does not exist.");
        groupUserRepository.deleteAll(groupUser);
    }

    @Transactional
    public void addTaskToGroup(Group group, Task task) {
        for (User user : getUsersFromGroup(group)) {
            List<UserTask> userTask = userTaskRepository.findByIdTaskAndIdUser(task.getIdTask(), user.getIdUser());
            if (userTask.isEmpty())
                userService.addTaskToUser(user, task);
        }
    }

    @Transactional
    public void removeTaskFromGroup(Group group, Task task) {
        for (User user : getUsersFromGroup(group)) {
            List<UserTask> userTask = userTaskRepository.findByIdTaskAndIdUser(task.getIdTask(), user.getIdUser());
            if (!userTask.isEmpty())
                userService.removeTaskFromUser(user, task);
        }
    }

    @Transactional(readOnly = true)
    public List<Group> findGroupsWithUser(User user) {
        List<Group> groups = groupRepository.findAll().stream().filter(group -> getUsersFromGroup(group).contains(user)).toList();
        if (groups.isEmpty())
            throw new BadRequestException("The user with idUser " + user.getIdUser() + " does not belong to any group.");
        return groups;
    }

    @Transactional(readOnly = true)
    public List<Group> findGroupsWithTask(Task task) {
        List<Group> groups = groupRepository.findAll().stream().filter(group -> getUsersFromGroup(group).stream().anyMatch(user -> userService.getTasksFromUser(user).contains(task))).toList();
        if (groups.isEmpty())
            throw new BadRequestException("The task with idTask " + task.getIdTask() + " does not belong to any group.");
        return groups;
    }

    @Transactional
    public void removeAllTasksFromGroup(Group group) {
        List<User> users = getUsersFromGroup(group);
        for (User user : users) {
            userService.removeAllTasksFromUser(user);
        }
    }
}
