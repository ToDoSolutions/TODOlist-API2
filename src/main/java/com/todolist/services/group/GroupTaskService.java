package com.todolist.services.group;

import com.todolist.entity.Group;
import com.todolist.entity.Task;
import com.todolist.exceptions.BadRequestException;
import com.todolist.repositories.GroupTaskRepository;
import com.todolist.services.TaskService;
import com.todolist.services.user.UserTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupTaskService {

    // Services ---------------------------------------------------------------
    private final GroupUserService groupUserService;
    private final TaskService taskService;
    private final UserTaskService userTaskService;

    // Repositories -----------------------------------------------------------
    private final GroupTaskRepository groupTaskRepository;

    // Finders ----------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<Group> findGroupsWithTask(Task task) {
        List<Group> groups = groupTaskRepository.findAllGroupsByTaskId(task.getId());
        if (groups.isEmpty())
            throw new BadRequestException("The task with idTask " + task.getId() + " does not belong to any group.");
        return groups;
    }

    @Transactional
    public List<Task> getTasksFromGroup(Group group) {
        List<Task> tasks = groupTaskRepository.findAllTasksFromGroupId(group.getId());
        if (tasks.isEmpty())
            throw new BadRequestException("The group with idGroup " + group.getId() + " does not have any task.");
        return tasks;
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
