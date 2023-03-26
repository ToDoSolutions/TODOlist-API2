package com.todolist.services;

import com.google.common.collect.Lists;
import com.todolist.component.AnalysisTable;
import com.todolist.component.PlanningTable;
import com.todolist.dtos.autodoc.RoleStatus;
import com.todolist.dtos.autodoc.clockify.ClockifyTask;
import com.todolist.dtos.autodoc.github.Issue;
import com.todolist.entity.Group;
import com.todolist.entity.Role;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.services.github.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AutoDocService {


    // Constants ---------------------------------------------------------------
    public static final String EURO = "€";

    // Services ---------------------------------------------------------------
    private final ClockifyService clockifyService;
    private final IssueService issueService;
    private final PlanningTable planningTable;
    private final AnalysisTable analysisTable;
    private final UserService userService;
    private final TaskService taskService;
    private final GroupService groupService;

    // Constructors -----------------------------------------------------------
    @Autowired
    public AutoDocService(ClockifyService clockifyService, IssueService issueService, PlanningTable planningTable, AnalysisTable analysisTable, UserService userService, TaskService taskService, GroupService groupService) {
        this.clockifyService = clockifyService;
        this.issueService = issueService;
        this.planningTable = planningTable;
        this.analysisTable = analysisTable;
        this.userService = userService;
        this.taskService = taskService;
        this.groupService = groupService;
    }

    // Methods ----------------------------------------------------------------
    @Transactional
    public void autoDoc(String repoName, String username) {
        taskService.deleteAll();
        groupIssuesWithHisTime(issueService.findByUsernameAndRepo(username, repoName), clockifyService.getTaskFromWorkspace(repoName, username), username, repoName);
    }

    @Transactional
    public void groupIssuesWithHisTime(List<Issue> issues, ClockifyTask[] clockifyTasks, String username, String repoName) {
        User user = userService.findUserByUsername(username);
        Group group = groupService.findGroupByName(repoName);
        for (Issue issue : issues) {
            for (ClockifyTask clockifyTask : clockifyTasks) {
                if (clockifyTask.getDescription().contains(issue.getTitle())) {
                    taskService.saveTask(issue, clockifyTask, group, user);
                }
            }
        }
    }

    private List<User> getEmployees(Map<String, List<Task>> timeTasks) {
        Set<User> employees = new HashSet<>();
        for (List<Task> tasks : timeTasks.values()) {
            for (Task task : tasks) {
                employees.add(task.getUser());
            }
        }
        return Lists.newArrayList(employees);
    }

    @Transactional
    public String[] getPlanning(String repoName, String username, String individual, String title) {
        autoDoc(repoName, username);
        Map<String, List<Task>> timeTasks = issueService.getTaskPerIssue(repoName, username).entrySet().stream()
                .filter(entry -> entry.getValue().stream().anyMatch(task -> task.getUser().getUsername().equals(individual) && task.getTitle().contains(title)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        List<User> employees = getEmployees(timeTasks);
        User individualEmployee = userService.findUserByUsername(individual);

        // Obtenemos la tabla para las tareas.
        String taskTable = planningTable.getTaskTable(timeTasks).serialize();

        // Obtenemos la tabla para los empleados.
        String personalTable = planningTable.getAllEmployeeTables(employees, title);

        // Obtenemos el coste total.
        double cost;
        if (Objects.equals(title, "I"))
            cost = userService.getIndividualCost(individualEmployee).values().stream().mapToDouble(v -> v).sum();
        else if (Objects.equals(title, "G"))
            cost = userService.getGroupCost(individualEmployee).values().stream().mapToDouble(v -> v).sum();
        else
            cost =  userService.getTotalCost(individualEmployee).values().stream().mapToDouble(v -> v).sum();

        // Nombres de los empleados.
        String names = planningTable.getNames(employees);

        // Roles del empleado.
        List<RoleStatus> roles = timeTasks.values().stream().flatMap(tasks -> tasks.stream().flatMap(task -> task.getRoles().stream())).map(Role::getStatus).distinct().toList();
        StringBuilder rolesString = new StringBuilder();
        for (var i = 0; i < roles.size(); i++) {
            rolesString.append(roles.get(i).toString().toLowerCase());
            if (i < roles.size() - 2)
                rolesString.append(", ");
            else if (i == roles.size() - 2)
                rolesString.append(" y ");
        }

        return new String[]{taskTable, personalTable, cost + EURO, names, rolesString.toString()};
    }

    @Transactional
    public String[] getPlanning(String repoName, String username, String title) {
        autoDoc(repoName, username);
        Map<String, List<Task>> timeTasks = issueService.getTaskPerIssue(repoName, username).entrySet().stream()
                .filter(entry -> entry.getValue().stream().anyMatch(task -> task.getTitle().contains(title)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        List<User> employees = getEmployees(timeTasks);
        Group group = groupService.findGroupByName(repoName);

        // Obtenemos la tabla para las tareas.
        String taskTable = planningTable.getTaskTable(timeTasks).serialize();

        // Obtenemos la tabla para los empleados.
        String personalTable = planningTable.getAllEmployeeTables(employees, title);

        // Obtenemos el coste total.
        double cost;
        if (Objects.equals(title, "I"))
            cost = groupService.getIndividualCost(group).values().stream().mapToDouble(v -> v).sum();
        else if (Objects.equals(title, "G"))
            cost = groupService.getGroupCost(group).values().stream().mapToDouble(v -> v).sum();
        else
            cost =  groupService.getTotalCost(group).values().stream().mapToDouble(v -> v).sum();

        // Nombres de los empleados.
        String names = planningTable.getNames(employees);

        return new String[]{taskTable, personalTable, cost + EURO, names};
    }

    @Transactional
    public String getAnalysis(String repoName, String username, String individual, String title) {
        autoDoc(repoName, username);
        Map<String, List<Task>> timeTasks = issueService.getTaskPerIssue(repoName, username).entrySet().stream()
                .filter(entry -> entry.getValue().stream().anyMatch(task -> task.getUser().getUsername().equals(individual) && task.getTitle().contains(title)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Obtenemos los enunciados.
        StringBuilder output = analysisTable.getStatements(timeTasks);

        // Obtenemos la tabla con los análisis.
        output.append(analysisTable.getAnalysis(timeTasks));

        return output.toString();
    }

    @Transactional
    public String getAnalysis(String repoName, String username, String title) {
        autoDoc(repoName, username);
        Map<String, List<Task>> timeTasks = issueService.getTaskPerIssue(repoName, username).entrySet().stream()
                .filter(entry -> entry.getValue().stream().anyMatch(task -> task.getTitle().contains(title)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Obtenemos los enunciados.
        StringBuilder output = analysisTable.getStatements(timeTasks);

        // Obtenemos la tabla con los análisis.
        output.append(analysisTable.getAnalysis(timeTasks));

        return output.toString();
    }
}
