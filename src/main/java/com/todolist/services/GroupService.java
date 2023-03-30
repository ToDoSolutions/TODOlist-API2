package com.todolist.services;

import com.todolist.component.DataManager;
import com.todolist.dtos.ShowUser;
import com.todolist.dtos.autodoc.RoleStatus;
import com.todolist.entity.Group;
import com.todolist.entity.GroupUser;
import com.todolist.entity.Task;
import com.todolist.entity.User;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupService {

    // Services ---------------------------------------------------------------
    private final GroupRepository groupRepository;

    private final GroupUserRepository groupUserRepository;

    private final UserService userService;
    private final TaskService taskService;

    // Components -------------------------------------------------------------
    private final DataManager dataManager;


    // Constructors -----------------------------------------------------------
    @Autowired
    public GroupService(GroupRepository groupRepository, GroupUserRepository groupUserRepository, UserService userService, TaskService taskService, DataManager dataManager) {
        this.groupRepository = groupRepository;
        this.groupUserRepository = groupUserRepository;
        this.userService = userService;
        this.taskService = taskService;
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
    public Group findGroupByName(String name) {
        return groupRepository.findByName(name).orElseThrow(() -> new NotFoundException("The group with name " + name + " does not exist."));
    }

    @Transactional(readOnly = true)
    public List<Group> findGroupsWithUser(User user) {
        List<Group> groups = groupRepository.findAll().stream().filter(group -> getUsersFromGroup(group).stream().map(User::getId).toList().contains(user.getId())).toList();
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
        return groupUserRepository.findByIdGroup(group.getId()).stream()
                .map(groupUser -> userService.findUserById(groupUser.getIdUser()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ShowUser> getShowUserFromGroup(Group group) {
        return getUsersFromGroup(group).stream().map(user -> new ShowUser(user, userService.getShowTaskFromUser(user))).toList();
    }

    @Transactional(readOnly = true)
    public Map<RoleStatus, Double> getCost(Group group, String title) {
        Map<RoleStatus, Double> cost = new EnumMap<>(RoleStatus.class);
        for (User user : getUsersFromGroup(group)) {
            Map<RoleStatus, Double> costUser = userService.getCost(user, title);
            for (RoleStatus role : RoleStatus.values()) {
                Double add = costUser.containsKey(role) ? costUser.get(role): 0;
                if (cost.containsKey(role))
                    cost.put(role, cost.get(role) + add);
                else
                    cost.put(role, add);
            }
        }
        return cost;
    }



    @Transactional(readOnly = true)
    public Map<RoleStatus, Double> getTotalCostByRole(Group group) {
        return getCost(group, "");
    }

    @Transactional(readOnly = true)
    public Map<RoleStatus, Double> getIndividualCost(Group group) {
        return getCost(group, "I");
    }

    @Transactional(readOnly = true)
    public Map<RoleStatus, Double> getGroupCost(Group group) {
        return getCost(group, "G");
    }

    @Transactional(readOnly = true)
    public Map<RoleStatus, Double> getCostByTitle(Group group, String title) {
        if (Objects.equals(title, "I"))
            return getIndividualCost(group);
        else if (Objects.equals(title, "G"))
            return getGroupCost(group);
        else
            return getTotalCostByRole(group);
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
        List<GroupUser> groupUser = groupUserRepository.findByIdGroup(group.getId());
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

    public void deleteAllTask(Group group) {
        List<Task> tasks = getTasksFromGroup(group);
        for (Task task : tasks) {
            taskService.deleteTask(task);
        }
    }

    private List<Task> getTasksFromGroup(Group group) {
        List<Task> tasks = new ArrayList<>();
        for (User user : getUsersFromGroup(group)) {
            tasks.addAll(userService.getTask(user));
        }
        return tasks;
    }
}
