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
    private final RoleService roleService;

    // Constructors -----------------------------------------------------------
    @Autowired
    public AutoDocService(ClockifyService clockifyService, IssueService issueService, PlanningTable planningTable, AnalysisTable analysisTable, UserService userService, TaskService taskService, GroupService groupService, RoleService roleService) {
        this.clockifyService = clockifyService;
        this.issueService = issueService;
        this.planningTable = planningTable;
        this.analysisTable = analysisTable;
        this.userService = userService;
        this.taskService = taskService;
        this.groupService = groupService;
        this.roleService = roleService;
    }

    // Methods ----------------------------------------------------------------
    @Transactional
    public void autoDoc(String repoName, String username) {
        taskService.deleteAll();
        groupIssuesWithHisTime(issueService.findByUsernameAndRepo(username, repoName), clockifyService.getTaskFromWorkspace(repoName, username), repoName);
    }

    @Transactional
    public void groupIssuesWithHisTime(List<Issue> issues, ClockifyTask[] clockifyTasks, String repoName) {
        Group group = groupService.findGroupByName(repoName);
        for (Issue issue : issues) {
            for (ClockifyTask clockifyTask : clockifyTasks) {
                if (clockifyTask.getDescription().contains(issue.getTitle())) {
                    User user = userService.findUserByIdClockify(clockifyTask.getUserId());
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
        Map<String, List<Task>> taskPerIssue = issueService.getTaskPerIssueFilter(repoName, username, title, individual);
        List<User> users = getEmployees(taskPerIssue);
        User individualEmployee = userService.findUserByUsername(individual);

        // Obtenemos la tabla para las tareas.
        String taskTable = planningTable.getTaskTable(taskPerIssue).serialize();

        // Obtenemos la tabla para los empleados.
        String personalTable = planningTable.getAllEmployeeTables(users, title);

        // Obtenemos el coste total.
        double cost = Math.round(userService.getCostByTitle(individualEmployee, title).values().stream()
                .map(v -> v == null ? 0: v) // TODO: Buscar una solución para no tener esta chapuza.
                .mapToDouble(v -> v).sum()*100)/100.;


        // Nombres de los empleados.
        String names = planningTable.getNames(users);

        // Roles del empleado.
        List<RoleStatus> roles = taskPerIssue.values().stream().flatMap(tasks -> tasks.stream().flatMap(task -> roleService.findRoleByTaskId(task.getId()).stream())).map(Role::getStatus).distinct().toList();
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
        Map<String, List<Task>> taskPerIssue = issueService.getTaskPerIssueFilter(repoName, username, title);
        List<User> users = getEmployees(taskPerIssue);
        Group group = groupService.findGroupByName(repoName);

        // Obtenemos la tabla para las tareas.
        String taskTable = planningTable.getTaskTable(taskPerIssue).serialize();

        // Obtenemos la tabla para los empleados.
        String personalTable = planningTable.getAllEmployeeTables(users, title);

        // Obtenemos el coste total.
        double cost = Math.round(groupService.getCostByTitle(group, title).values()
                .stream()
                .map(v -> v == null ? 0: v) // TODO: Buscar una solución para no tener esta chapuza.
                .mapToDouble(v -> v).sum()+100)/100.;

        // Nombres de los empleados.
        String names = planningTable.getNames(users);

        return new String[]{taskTable, personalTable, cost + EURO, names};
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
