package com.todolist.repository;

import com.todolist.dtos.ShowGroup;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import com.todolist.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("repositories")
public class Repositories {

    @Autowired
    @Qualifier("groupRepository")
    private GroupRepository groupRepository;

    @Autowired
    @Qualifier("userRepository")
    private UserRepository userRepository;

    @Autowired
    @Qualifier("taskRepository")
    private TaskRepository taskRepository;

    @Autowired
    @Qualifier("userTaskRepository")
    private UserTaskRepository userTaskRepository;

    @Autowired
    @Qualifier("groupUserRepository")
    private GroupUserRepository groupUserRepository;

    private String path;

    public void setPath(String path) {
        this.path = path;
    }

    // Task
    public List<ShowTask> findAllShowTasks(Sort sort) {
        return taskRepository.findAll(sort).stream().map(ShowTask::new).collect(Collectors.toList());
    }

    public Task findTaskById(Long idTask) {
        return taskRepository.findById(idTask).orElse(null);
    }

    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(Task task) {
        taskRepository.delete(task);
    }

    // User
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
        userRepository.deleteById(user);
    }

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

    public List<ShowGroup> getShowGroupsFromUser(User user) {
        return getGroupsFromUser(user).stream().map(group -> new ShowGroup(group, getShowUserFromGroup(group))).collect(Collectors.toList());
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

    public void removeUserFromAllGroups(User user) {
        List<GroupUser> groupUser = groupUserRepository.findByIdUser(user.getIdUser());
        groupUserRepository.deleteAll(groupUser);
    }

    // Group
    public List<ShowGroup> findAllShowGroups(Sort sort) {
        return groupRepository.findAll(sort).stream().map(group -> new ShowGroup(group, getShowUserFromGroup(group))).collect(Collectors.toList());
    }

    public Group findGroupById(Long idGroup) {
        return groupRepository.findById(idGroup).orElse(null);
    }

    public Group saveGroup(Group group) {
        return groupRepository.save(group);
    }

    public void deleteGroup(Group group) {
        groupRepository.delete(group);
    }

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
