package com.todolist.services.autodoc;

import com.todolist.dtos.autodoc.Area;
import com.todolist.dtos.autodoc.Request;
import com.todolist.entity.Group;
import com.todolist.entity.Role;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.repositories.PlanningRepository;
import com.todolist.services.RoleService;
import com.todolist.services.github.IssueService;
import com.todolist.services.group.GroupService;
import com.todolist.services.group.GroupTaskService;
import com.todolist.services.group.GroupUserService;
import com.todolist.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PlanningService {
    private final IssueService issueService;
    private final UserService userService;
    private final GroupService groupService;
    private final RoleService roleService;
    private final AutoDocService autoDocService;
    private final GroupUserService groupUserService;
    private final PlanningRepository planningRepository;
    private final GroupTaskService groupTaskService;

    @Autowired
    public PlanningService(IssueService issueService, UserService userService, GroupService groupService, RoleService roleService, AutoDocService autoDocService, GroupUserService groupUserService, PlanningRepository planningRepository, GroupTaskService groupTaskService) {
        this.issueService = issueService;
        this.userService = userService;
        this.groupService = groupService;
        this.roleService = roleService;
        this.autoDocService = autoDocService;
        this.groupUserService = groupUserService;
        this.planningRepository = planningRepository;
        this.groupTaskService = groupTaskService;
    }

    public record PlanningData(Map<String, List<Task>> taskPerIssue, List<User> users, Group group, double cost) {
    }

    // Main -------------------------------------------------------
    @Transactional
    public PlanningData getPlanningData(Request request) throws IOException {
        autoDocService.autoDoc(request);
        Map<String, List<Task>> taskPerIssue = issueService.getTaskPerIssueFilter(request);
        List<User> users = getEmployees(taskPerIssue);
        System.out.println(users);
        Group group = groupService.findGroupByName(request.getRepoName());
        double cost = calculateCost(request, group);
        return new PlanningData(taskPerIssue, users, group, cost);
    }


    @Transactional
    public double calculateCost(Request request, Group group) {
        if (request.getIndividual() != null) {
            User individualEmployee = userService.findUserByUsername(request.getIndividual());
            return getTotalCostByRole(individualEmployee, group, request.getArea()).values()
                    .stream().mapToDouble(v -> v == null ? 0 : v).sum();
        } else {
            return getGroupCost(group, request.getArea()).values()
                    .stream().mapToDouble(v -> v == null ? 0 : v).sum();
        }
    }

    // Grupal -------------------------------------------------------
    @Transactional(readOnly = true)
    public Map<String, Double> getGroupCost(Group group, Area area) {
        Map<String, Double> cost = new HashMap<>();
        for (User user : groupUserService.getUsersFromGroup(group)) {
            getTotalCostByRole(user, group, area).forEach((role, add) -> cost.merge(role, add, Double::sum));
        }
        return cost;
    }

    // User -------------------------------------------------------
    @Transactional(readOnly = true)
    public Map<String, Double> getTotalCostByRole(User user, Group group, Area area) {
        return getTaskFromGroupAndUser(user, group, area).stream()
                .flatMap(task -> roleService.findRoleByTaskId(task.getId()).stream())
                .collect(Collectors.groupingBy(Role::getName, Collectors.summingDouble(Role::getSalary)));
    }

    private List<Task> getTaskFromGroupAndUser(User user, Group group, Area area) {
        List<Task> allMyTasks = planningRepository.findAllTaskByUserId(user.getId());
        List<Task> groupTask = groupTaskService.getTasksFromGroup(group);
        return allMyTasks.stream()
                .filter(task -> groupTask.contains(task) && isTaskInArea(task, area))
                .toList();
    }

    private boolean isTaskInArea(Task task, Area area) {
        return switch (area) {
            case INDIVIDUAL -> task.getStudent() != 0;
            case GROUP -> task.getStudent() == 0;
            case ALL -> true;
        };
    }

    // Other -------------------------------------------------------

    private List<User> getEmployees(Map<String, List<Task>> timeTasks) {
        return timeTasks.values().stream()
                .flatMap(List::stream)
                .map(Task::getUser)
                .distinct()
                .toList();
    }
}
