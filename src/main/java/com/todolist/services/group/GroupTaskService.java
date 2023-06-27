package com.todolist.services.group;

import com.todolist.entity.Group;
import com.todolist.entity.Task;
import com.todolist.exceptions.BadRequestException;
import com.todolist.repositories.GroupRepository;
import com.todolist.services.TaskService;
import com.todolist.services.user.UserService;
import com.todolist.services.user.UserTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupTaskService {

    // Services ---------------------------------------------------------------
    private final GroupUserService groupUserService;
    private final UserService userService;
    private final TaskService taskService;
    private final UserTaskService userTaskService;

    // Repositories -----------------------------------------------------------
    private final GroupRepository groupRepository;

    // Constructors -----------------------------------------------------------
    @Autowired
    public GroupTaskService(GroupUserService groupUserService, UserService userService, TaskService taskService, UserTaskService userTaskService, GroupRepository groupRepository) {
        this.groupUserService = groupUserService;
        this.userService = userService;
        this.taskService = taskService;
        this.userTaskService = userTaskService;
        this.groupRepository = groupRepository;
    }

    // Finders ----------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<Group> findGroupsWithTask(Task task) {
        List<Group> groups = groupRepository.findAll().stream()
                .filter(group -> hasGroupTheTask(group, task))
                .toList();
        if (groups.isEmpty()) {
            throw new BadRequestException("The task with idTask " + task.getId() + " does not belong to any group.");
        }
        return groups;
    }

    @Transactional
    public List<Task> getTasksFromGroup(Group group) {
        return groupUserService.getUsersFromGroup(group).stream()
                .flatMap(user -> userTaskService.getTasksFromUser(user).stream())
                .collect(Collectors.toList());
    }

    // Save and Delete --------------------------------------------------------
    @Transactional
    public void addTaskToGroup(Group group, Task task) {
        groupUserService.getUsersFromGroup(group).forEach(user -> userTaskService.addTaskToUser(user, task));
    }

    @Transactional
    public void removeTaskFromGroup(Group group, Task task) {
        groupUserService.getUsersFromGroup(group).forEach(user -> userTaskService.removeTaskFromUser(user, task));
    }

    @Transactional
    public void removeAllTasksFromGroup(Group group) {
        groupUserService.getUsersFromGroup(group).forEach(userTaskService::removeAllTasksFromUser);
    }

    @Transactional
    public void deleteAllTasks(Group group) {
        getTasksFromGroup(group).forEach(taskService::deleteTask);
    }

    // Others -----------------------------------------------------------------
    @Transactional
    public boolean hasGroupTheTask(Group group, Task task) {
        return getTasksFromGroup(group).stream()
                .anyMatch(t -> t.getId().equals(task.getId()));
    }
}
