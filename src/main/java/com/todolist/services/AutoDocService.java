package com.todolist.services;

import com.todolist.component.AnalysisTable;
import com.todolist.component.PlanningTable;
import com.todolist.dtos.autodoc.clockify.ClockifyTask;
import com.todolist.entity.Group;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import lombok.AllArgsConstructor;
import org.kohsuke.github.GHIssue;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
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
    private final RoleService roleService;

    // Methods ----------------------------------------------------------------
    @Transactional
    public void autoDoc(String repoName, String username) {
        Group group = groupService.findGroupByName(repoName);
        groupService.deleteAllTask(group);
        groupIssuesWithHisTime(issueService.findByUsernameAndRepo(username, repoName), clockifyService.getTaskFromWorkspace(repoName, username), repoName);
    }

    @Transactional
    public void groupIssuesWithHisTime(List<GHIssue> issues, List<ClockifyTask> clockifyTasks, String repoName) {
        Group group = groupService.findGroupByName(repoName);
        issues.forEach(issue ->
                clockifyTasks.stream()
                        .filter(clockifyTask -> clockifyTask.getDescription().contains(issue.getTitle()))
                        .forEach(clockifyTask -> {
                            User user = userService.findUserByIdClockify(clockifyTask.getUserId());
                            taskService.saveTask(issue, clockifyTask, group, user);
                        }));
    }

    private List<User> getEmployees(Map<String, List<Task>> timeTasks) {
        return timeTasks.values().stream()
                .flatMap(List::stream)
                .map(Task::getUser)
                .distinct()
                .toList();
    }

    @Transactional
    public String[] getPlanning(String repoName, String username, String individual, String title) {
        autoDoc(repoName, username);
        Map<String, List<Task>> taskPerIssue = issueService.getTaskPerIssueFilter(repoName, username, title, individual);
        List<User> users = getEmployees(taskPerIssue);
        User individualEmployee = userService.findUserByUsername(individual);

        // Obtenemos la tabla para las tareas.
        String taskTable = planningTable.getTaskTable(taskPerIssue).serialize();

        // Obtenemos la tabla para los empleados.
        String personalTable = planningTable.getAllEmployeeTables(users, title);

        // Obtenemos el coste total.
        double cost = userService.getCostByTitle(individualEmployee, title).values()
                .stream().mapToDouble(v -> v == null ? 0 : v).sum();


        // Nombres de los empleados.
        String names = planningTable.getNames(users);

        // Roles del empleado.
        String rolesString = getRolesString(taskPerIssue);

        return new String[]{taskTable, personalTable, String.format("%.2f %s", cost, EURO), names, rolesString};
    }

    private String getRolesString(Map<String, List<Task>> taskPerIssue) {
        return taskPerIssue.values().stream()
                .flatMap(tasks -> tasks.stream()
                        .flatMap(task -> roleService.findRoleByTaskId(task.getId()).stream()))
                .map(role -> role.getStatus().toString().toLowerCase())
                .distinct()
                .collect(Collectors.joining(", ", "", " y "));
    }

    @Transactional
    public String[] getPlanning(String repoName, String username, String title) {
        autoDoc(repoName, username);
        Map<String, List<Task>> taskPerIssue = issueService.getTaskPerIssueFilter(repoName, username, title);
        List<User> users = getEmployees(taskPerIssue);
        Group group = groupService.findGroupByName(repoName);

        // Obtenemos la tabla para las tareas.
        String taskTable = planningTable.getTaskTable(taskPerIssue).serialize();

        // Obtenemos la tabla para los empleados.
        String personalTable = planningTable.getAllEmployeeTables(users, title);

        // Obtenemos el coste total.
        double cost = groupService.getCostByTitle(group, title).values()
                .stream().mapToDouble(v -> v == null ? 0 : v).sum();


        // Nombres de los empleados.
        String names = planningTable.getNames(users);

        return new String[]{taskTable, personalTable, String.format("%.2f %s", cost, EURO), names};
    }

    @Transactional
    public String getAnalysis(String repoName, String username, String individual, String title) {
        autoDoc(repoName, username);
        Map<String, List<Task>> timeTasks = issueService.getTaskPerIssueFilter(repoName, username, title, individual);

        // Obtenemos los enunciados.
        StringBuilder output = analysisTable.getStatements(timeTasks);

        // Obtenemos la tabla con los análisis.
        output.append(analysisTable.getAnalysis(timeTasks));

        return output.toString();
    }

    @Transactional
    public String getAnalysis(String repoName, String username, String title) {
        autoDoc(repoName, username);
        Map<String, List<Task>> timeTasks = issueService.getTaskPerIssueFilter(repoName, username, title);

        // Obtenemos los enunciados.
        StringBuilder output = analysisTable.getStatements(timeTasks);

        // Obtenemos la tabla con los análisis.
        output.append(analysisTable.getAnalysis(timeTasks));

        return output.toString();
    }
}
