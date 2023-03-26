package com.todolist.services;

import com.todolist.component.DataManager;
import com.todolist.dtos.ShowUser;
import com.todolist.entity.*;
import com.todolist.exceptions.BadRequestException;
import com.todolist.exceptions.NotFoundException;
import com.todolist.repositories.GroupRepository;
import com.todolist.repositories.GroupUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {

    // Services ---------------------------------------------------------------
    private final GroupRepository groupRepository;

    private final GroupUserRepository groupUserRepository;

    private final UserService userService;

    // Components -------------------------------------------------------------
    private final DataManager dataManager;


    // Constructors -----------------------------------------------------------
    @Autowired
    public GroupService(GroupRepository groupRepository, GroupUserRepository groupUserRepository, UserService userService, DataManager dataManager) {
        this.groupRepository = groupRepository;
        this.groupUserRepository = groupUserRepository;
        this.userService = userService;
        this.dataManager = dataManager;
    }

    // Populate database ------------------------------------------------------
    @PostConstruct
    @Transactional
    public void init() throws IOException {
        List<Group> groups = dataManager.loadGroup();
        groupRepository.saveAll(groups);
        List<GroupUser> groupUsers = dataManager.loadGroupUser();
        groupUserRepository.saveAll(groupUsers);
    }

    /**
     * GROUPS
     */
    // Finders ----------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<Group> findAllGroups(Sort sort) {
        return groupRepository.findAll(sort);
    }

    @Transactional(readOnly = true)
    public Group findGroupById(Integer idGroup) {
        return groupRepository.findById(idGroup).orElseThrow(() -> new NotFoundException("The group with idGroup " + idGroup + " does not exist."));
    }

    @Transactional(readOnly = true)
    public List<Group> findGroupsWithUser(User user) {
        List<Group> groups = groupRepository.findAll().stream().filter(group -> getUsersFromGroup(group).contains(user)).toList();
        if (groups.isEmpty())
            throw new BadRequestException("The user with idUser " + user.getId() + " does not belong to any group.");
        return groups;
    }

    @Transactional(readOnly = true)
    public List<Group> findGroupsWithTask(Task task) {
        List<Group> groups = groupRepository.findAll().stream().filter(group -> getUsersFromGroup(group).stream().anyMatch(user -> user.getTasks().contains(task))).toList();
        if (groups.isEmpty())
            throw new BadRequestException("The task with idTask " + task.getId() + " does not belong to any group.");
        return groups;
    }

    @Transactional(readOnly = true)
    public Group findTaskByTitle(String username, String repoName) {
        return findGroupsWithUser(userService.findUserByUsername(username)).stream()
                .filter(group -> group.getName().equals(repoName))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("The group with name " + repoName + " does not exist."));
    }

    @Transactional(readOnly = true)
    public Long getNumTasks(Group group) {
        return (long) getUsersFromGroup(group).stream()
                .flatMap(user -> user.getTasks().stream())
                .collect(Collectors.toSet()).size();
    }

    // Save and delete --------------------------------------------------------
    @Transactional
    public Group saveGroup(Group group) {
        if (group.getCreatedDate() == null) group.setCreatedDate(LocalDate.now());
        return groupRepository.save(group);
    }

    @Transactional
    public void deleteGroup(Group group) {
        groupRepository.delete(group);
    }

    /**
     * USERS
     */
    // Finders ----------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<User> getUsersFromGroup(Group group) {
        return groupUserRepository.findById(group.getId()).stream()
                .map(groupUser -> userService.findUserById(groupUser.getIdUser()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ShowUser> getShowUserFromGroup(Group group) {
        return getUsersFromGroup(group).stream().map(user -> new ShowUser(user, userService.getShowTaskFromUser(user))).toList();
    }

    // Save and delete --------------------------------------------------------
    @Transactional
    public void addUserToGroup(Group group, User user) {
        groupUserRepository.save(new GroupUser(group.getId(), user.getId()));
    }

    @Transactional
    public void removeUserFromGroup(Group group, User user) {
        List<GroupUser> groupUser = groupUserRepository.findByIdAndIdUser(group.getId(), user.getId());
        groupUserRepository.deleteAll(groupUser);
    }

    @Transactional
    public void removeAllUsersFromGroup(Group group) {
        List<GroupUser> groupUser = groupUserRepository.findById(group.getId());
        if (groupUser == null)
            throw new NotFoundException("The group with idGroup " + group.getId() + " does not exist.");
        groupUserRepository.deleteAll(groupUser);
    }

    /**
     * TASKS
     */
    // Save and delete --------------------------------------------------------
    @Transactional
    public void addTaskToGroup(Group group, Task task) {
        for (User user : getUsersFromGroup(group)) {
            userService.addTaskToUser(user, task);
        }
    }

    @Transactional
    public void removeTaskFromGroup(Group group, Task task) {
        for (User user : getUsersFromGroup(group)) {
            userService.removeTaskFromUser(user, task);
        }
    }

    @Transactional
    public void removeAllTasksFromGroup(Group group) {
        List<User> users = getUsersFromGroup(group);
        for (User user : users) {
            userService.removeAllTasksFromUser(user);
        }
    }
}
