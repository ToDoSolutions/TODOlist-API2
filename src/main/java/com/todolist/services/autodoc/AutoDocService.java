package com.todolist.services.autodoc;

import com.todolist.dtos.autodoc.Request;
import com.todolist.dtos.autodoc.clockify.ClockifyTask;
import com.todolist.entity.Group;
import com.todolist.entity.User;
import com.todolist.services.ClockifyService;
import com.todolist.services.TaskService;
import com.todolist.services.github.IssueService;
import com.todolist.services.group.GroupService;
import com.todolist.services.group.GroupTaskService;
import com.todolist.services.user.UserService;
import org.kohsuke.github.GHIssue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
public class AutoDocService {

    // Services ---------------------------------------------------------------
    private final ClockifyService clockifyService;
    private final IssueService issueService;
    private final UserService userService;
    private final TaskService taskService;
    private final GroupService groupService;
    private final GroupTaskService groupTaskService;


    // Constructors -----------------------------------------------------------
    @Autowired
    public AutoDocService(ClockifyService clockifyService, IssueService issueService, UserService userService, TaskService taskService, GroupService groupService, GroupTaskService groupTaskService) {
        this.clockifyService = clockifyService;
        this.issueService = issueService;
        this.userService = userService;
        this.taskService = taskService;
        this.groupService = groupService;
        this.groupTaskService = groupTaskService;
    }

    // Methods ----------------------------------------------------------------
    @Transactional
    public void autoDoc(Request request) throws IOException {
        groupIssuesWithTime(request);
    }

    @Transactional
    public void groupIssuesWithTime(Request request) throws IOException {
        Group group = groupService.findGroupByName(request.getRepoName());
        if (Boolean.TRUE.equals(request.isIndividual())) {
            groupService.resetRolesForUser(request.getIndividual(), group);
        } else {
            groupService.resetRolesForGroup(group);
        }
        List<ClockifyTask> clockifyTasks = clockifyService.getTaskFromWorkspace(request.getRepoName(), request.getUsername());
        if (isANewTask(clockifyTasks, group)) {
            List<GHIssue> issues = issueService.findByUsernameAndRepo(request);
            for (GHIssue issue : issues) {
                for (ClockifyTask clockifyTask : clockifyTasks) {
                    if (clockifyTask.getDescription().contains(issue.getTitle())) {
                        User user = userService.findUserByIdClockify(clockifyTask.getUserId());
                        taskService.saveTask(issue, clockifyTask, group, user);
                    }
                }
            }
        } else {
            for (ClockifyTask clockifyTask : clockifyTasks) {
                User user = userService.findUserByIdClockify(clockifyTask.getUserId());
                taskService.saveTask(clockifyTask, group, user);
            }
        }
    }

    public boolean isANewTask(List<ClockifyTask> clockifyTasks, Group group) {
        return clockifyTasks.stream()
                .noneMatch(clockifyTask -> {
                    User user = userService.findUserByIdClockify(clockifyTask.getUserId());
                    return groupTaskService.getTasksFromGroup(group).stream()
                            .filter(task -> task.getUser().equals(user))
                            .anyMatch(task -> task.getTitleIssue().equals(clockifyTask.getDescription()));
                });

    }
}