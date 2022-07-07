package com.todolist.repository;

import com.todolist.entity.*;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("repositories")
public class Repositories {

    @Autowired
    @Qualifier("groupRepository")
    public GroupRepository groupRepository;

    @Autowired
    @Qualifier("userRepository")
    public UserRepository userRepository;

    @Autowired
    @Qualifier("taskRepository")
    public TaskRepository taskRepository;

    @Autowired
    @Qualifier("userTaskRepository")
    public UserTaskRepository userTaskRepository;

    @Autowired
    @Qualifier("groupUserRepository")
    private GroupUserRepository groupUserRepository;

    // User
    public List<Task> getTasksFromUser(User user) {
        return userTaskRepository.findByIdUser(user.getIdUser()).stream()
                .map(userTask -> taskRepository.findById(userTask.getIdTask()).orElse(null))
                .collect(Collectors.toList());
    }

    public List<ShowTask> getShowTaskFromUser(User user) {
        return getTasksFromUser(user).stream().map(ShowTask::new).collect(Collectors.toList());
    }

    public List<Group> getGroupsFromUser(User user) {
        return groupUserRepository.findByIdUser(user.getIdUser()).stream()
                .map(groupUser -> groupRepository.findById(groupUser.getIdGroup()).orElse(null))
                .collect(Collectors.toList());
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

    // Group
    public List<User> getUsersFromGroup(Group group) {
        return groupUserRepository.findByIdGroup(group.getIdGroup()).stream()
                .map(groupUser -> userRepository.findByIdUser(groupUser.getIdUser()))
                .collect(Collectors.toList());
    }

    public List<ShowUser> getShowUserFromGroup(Group group) {
        return getUsersFromGroup(group).stream().map(user -> new ShowUser(user, getShowTaskFromUser(user))).collect(Collectors.toList());
    }

    public void addUserToGroup(Group group, User user) {
        List<GroupUser> groupUsers = groupUserRepository.findByIdGroupAndIdUser(group.getIdGroup(), user.getIdUser());
        if (groupUsers.isEmpty())
            throw new NullPointerException("The user with idUser " + user.getIdUser() + " does not belong to the group with idGroup " + group.getIdGroup() + ".|method: addUserToGroup");
        groupUserRepository.deleteAll(groupUsers);
    }

    public void removeUserFromGroup(Group group, User user) {
        List<GroupUser> groupUser = groupUserRepository.findByIdGroupAndIdUser(group.getIdGroup(), user.getIdUser());
        groupUserRepository.deleteAll(groupUser);
    }

    public void removeAllUsersFromGroup(Group group) {
        List<GroupUser> groupUser = groupUserRepository.findByIdGroup(group.getIdGroup());
        if (groupUser == null)
            throw new NullPointerException("The group with idGroup " + group.getIdGroup() + " does not have any user.|method: removeAllUsersFromGroup");
        groupUserRepository.deleteAll(groupUser);
    }

    public void addTaskToGroup(Group group, Task task) {
        for (User user : getUsersFromGroup(group)) {
            List<UserTask> userTask = userTaskRepository.findByIdTaskAndIdUser(task.getIdTask(), user.getIdUser());
            if (userTask.isEmpty())
                addTaskToUser(user, task);
        }
    }

    public void removeTaskFromGroup(Group group, Task task) {
        for (User user : getUsersFromGroup(group)) {
            List<UserTask> userTask = userTaskRepository.findByIdTaskAndIdUser(task.getIdTask(), user.getIdUser());
            if (!userTask.isEmpty())
                removeTaskFromUser(user, task);
        }
    }

    public void removeAllTasksFromGroup(Group group) {
        List<User> users = getUsersFromGroup(group);
        for (User user : users) {
            removeAllTasksFromUser(user);
        }
    }

    public void addTasktoAllUser(Group group, Task task) {
        List<User> users = getUsersFromGroup(group);
        for (User user : users) {
            addTaskToUser(user, task);
        }
    }
}
