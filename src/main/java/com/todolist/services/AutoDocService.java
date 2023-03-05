package com.todolist.services;

import com.google.common.collect.Lists;
import com.todolist.entity.User;
import com.todolist.entity.autodoc.Employee;
import com.todolist.entity.autodoc.Role;
import com.todolist.entity.autodoc.TimeTask;
import com.todolist.entity.autodoc.clockify.ClockifyTask;
import com.todolist.entity.autodoc.github.Issue;
import com.todolist.services.github.IssueService;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.emphasis.BoldText;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AutoDocService {

    public static final Object[] HEADER_PLANNING = {"Título", "Descripción", "Responsables", "Rol", "Tiempo planificado", "Tiempo real", "Coste"};
    public static final Object[] HEADER_ANALYSIS = {"ID", "Conclusiones", "Decisiones tomadas"};
    public static final String SEPARATOR_ID = ":";
    public static final String JUMP_LINE = "\n";
    private final ClockifyService clockifyService;
    private final IssueService issueService;


    public AutoDocService(ClockifyService clockifyService, IssueService issueService) {
        this.clockifyService = clockifyService;
        this.issueService = issueService;
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
        List<TimeTask> timeTasks;
        if (Objects.equals(individual, "all"))
            timeTasks = autoDoc(repoName, username);
        else
            timeTasks = autoDoc(repoName, username).stream().filter(timeTask -> timeTask.getEmployees().stream().anyMatch(employee -> employee.getName().equals(individual))).toList();
        List<Employee> employees = getEmployees(timeTasks);
        Table.Builder taskTable = new Table.Builder()
                .withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                .addRow(HEADER_PLANNING);
        // Obtenemos la tabla con los costes de cada tarea.
        for (TimeTask timeTask : timeTasks) {
            taskTable.addRow(timeTask.getTitle().trim(), timeTask.getDescription() != null ? timeTask.getDescription().trim().replace(JUMP_LINE,""): null, timeTask.getEmployees().stream().map(Employee::getName).reduce((s, s2) -> s + ", " + s2).orElse("")
                    , timeTask.getRoles().stream().map(Role::toString).reduce((s, s2) -> s + ", " + s2).orElse(""), "x"
                    , timeTask.getDuration().toHours() + " horas y " + timeTask.getDuration().toMinutes() % 60 + " minutos"
                    , Math.round(timeTask.getCost()*100)/100. + "€");
        }
        StringBuilder personalTable = new StringBuilder();
        double cost = 0;
        // Obtenemos la tabla para los empleados.
        for (Employee employee : employees) {
            personalTable.append(JUMP_LINE).append(new Heading(employee.getName(), 3)).append(JUMP_LINE);
            personalTable.append(new Table.Builder().withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                    .addRow("Rol", "Coste")
                    .addRow("Desarrollador", employee.getSalaryByRole(Role.DEVELOPER) + "€")
                    .addRow("Analista", employee.getSalaryByRole(Role.ANALYST) + "€")
                    .addRow("Tester", employee.getSalaryByRole(Role.TESTER) + "€")
                    .addRow("Diseñador", employee.getSalaryByRole(Role.MANAGER) + "€")
                    .addRow("Operador", employee.getSalaryByRole(Role.OPERATOR) + "€")
                    .build().serialize());
            cost += employee.getSalary().values().stream().mapToDouble(i -> i).sum();
        }
        return new String[]{taskTable.build().serialize(), personalTable.toString(), cost + "€"};
    }

    public String getAnalysis(String repoName, String username, String individual) {
        List<TimeTask> timeTasks;
        if (Objects.equals(individual, "all"))
            timeTasks = autoDoc(repoName, username);
        else
            timeTasks = autoDoc(repoName, username).stream().filter(timeTask -> timeTask.getEmployees().stream().anyMatch(employee -> employee.getName().equals(individual))).toList();
        StringBuilder output = new StringBuilder(new Heading("Enunciados", 3).toString()).append(JUMP_LINE);
        Table.Builder table = new Table.Builder()
                .withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                .addRow(HEADER_ANALYSIS);
        for (TimeTask timeTask : timeTasks) {
            String[] text = timeTask.getTitle().split(SEPARATOR_ID);
            String id = text[0].trim();
            String body = text[1].trim();
            output.append("- ").append(new BoldText(id)).append("-").append(body).append(JUMP_LINE);
            table.addRow(id, timeTask.getConlusion().trim(), timeTask.getDecision().trim());
        }
        output.append(JUMP_LINE).append(new Heading("Análisis", 3)).append(JUMP_LINE).append(table.build().serialize());
        return output.toString();
    }
}
