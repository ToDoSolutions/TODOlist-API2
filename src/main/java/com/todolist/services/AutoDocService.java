package com.todolist.services;

import com.google.common.collect.Lists;
import com.todolist.component.AnalysisTable;
import com.todolist.component.PlanningTable;
import com.todolist.entity.User;
import com.todolist.entity.autodoc.Employee;
import com.todolist.entity.autodoc.Role;
import com.todolist.entity.autodoc.TimeTask;
import com.todolist.entity.autodoc.clockify.ClockifyTask;
import com.todolist.entity.autodoc.github.Issue;
import com.todolist.services.github.IssueService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AutoDocService {


    public static final String ALL = "all";
    public static final String EURO = "€";
    private final ClockifyService clockifyService;
    private final IssueService issueService;
    private final PlanningTable planningTable;
    private final AnalysisTable analysisTable;


    public AutoDocService(ClockifyService clockifyService, IssueService issueService, PlanningTable planningTable, AnalysisTable analysisTable) {
        this.clockifyService = clockifyService;
        this.issueService = issueService;
        this.planningTable = planningTable;
        this.analysisTable = analysisTable;
    }

    public List<TimeTask> autoDoc(String repoName, String username) {
        Map<Issue, ClockifyTask[]> map = groupIssuesWithHisTime(issueService.findByUsernameAndRepo(username, repoName), clockifyService.getTaskFromWorkspace(repoName));
        return map.entrySet().stream().map(entry -> createTimeTask(entry, repoName)).toList();
    }

    public Map<Issue, ClockifyTask[]> groupIssuesWithHisTime(Issue[] issues, ClockifyTask[] clockifyTasks) {
        return Stream.of(issues).collect(
                Collectors.toMap(issue -> issue, issue -> Stream.of(clockifyTasks).filter(clockifyTask -> clockifyTask.getDescription().contains(issue.title)).toArray(ClockifyTask[]::new))
        );
    }

    public TimeTask createTimeTask(Map.Entry<Issue, ClockifyTask[]> entry, String repoName) {
        Issue issue = entry.getKey();
        ClockifyTask[] clockifyTask = entry.getValue();
        Duration duration = Duration.ZERO;
        Set<Role> allRoles = new HashSet<>();
        List<User> users = issue.assignees.stream().map(issueService::getUserAssignedToIssue).toList();
        List<Employee> employees = users.stream().map(user -> new Employee(user.getFullName(), user.getClockifyId())).toList();
        for (ClockifyTask task : clockifyTask) {
            Employee employee = findEmployeeClockifyTask(employees, task);
            List<Role> roles = task.getTagIds().stream().map(tagId -> clockifyService.getRoleFromClockify(repoName, tagId)).distinct().toList();
            duration = duration.plus(task.calculateSalary(roles, duration, employee));
            allRoles.addAll(roles);
        }
        return new TimeTask(issue.body, issue.title, duration, allRoles, employees);
    }

    public Employee findEmployeeClockifyTask(List<Employee> employees, ClockifyTask clockifyTask) {
        return employees.stream().filter(employee -> employee.getClockifyId().equals(clockifyTask.getUserId())).findFirst().orElse(null);
    }

    public List<Employee> getEmployees(List<TimeTask> timeTasks) {
        List<Employee> employeesTime = timeTasks.stream().flatMap(timeTask -> timeTask.getEmployees().stream()).toList();
        List<Employee> employees = Lists.newArrayList();
        for (Employee employeeTime : employeesTime) {
            Employee employee = employees.stream().filter(employee1 -> employee1.getName().equals(employeeTime.getName())).findFirst().orElse(null);
            if (employee == null)
                employees.add(employeeTime);
            else
                employee.updateSalary(employeeTime);
        }
        return employees;
    }

    public String[] getPlanning(String repoName, String username, String individual) {
        List<TimeTask> timeTasks = getTimeTasks(individual, repoName, username);
        List<Employee> employees = getEmployees(timeTasks);

        // Obtenemos la tabla para las tareas.
        String taskTable = planningTable.getTaskTable(timeTasks).serialize();

        // Obtenemos la tabla para los empleados.
        String personalTable = planningTable.getAllEmployeeTables(employees);

        // Obtenemos el coste total.
        double cost = employees.stream().mapToDouble(employee -> employee.getSalary().values().stream().mapToDouble(i -> i).sum()).sum();

        return new String[]{taskTable, personalTable, cost + EURO};
    }

    public String getAnalysis(String repoName, String username, String individual) {
        List<TimeTask> timeTasks = getTimeTasks(individual, repoName, username);

        // Obtenemos los enunciados.
        StringBuilder output = analysisTable.getStatements(timeTasks);

        // Obtenemos la tabla con los análisis.
        output.append(analysisTable.getAnalysis(timeTasks));

        return output.toString();
    }

    private List<TimeTask> getTimeTasks(String individual, String repoName, String username) {
        if (Objects.equals(individual, ALL))
            return autoDoc(repoName, username);
        else
            return autoDoc(repoName, username).stream().filter(timeTask -> timeTask.getEmployees().stream().anyMatch(employee -> employee.getName().equals(individual))).toList();
    }
}
