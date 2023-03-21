package com.todolist.services;

import com.google.common.collect.Lists;
import com.todolist.component.AnalysisTable;
import com.todolist.component.PlanningTable;
import com.todolist.dtos.autodoc.Employee;
import com.todolist.dtos.autodoc.RoleStatus;
import com.todolist.dtos.autodoc.TimeTask;
import com.todolist.dtos.autodoc.clockify.ClockifyTask;
import com.todolist.dtos.autodoc.github.Issue;
import com.todolist.entity.User;
import com.todolist.services.github.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AutoDocService {


    // Constants ---------------------------------------------------------------
    public static final String EURO = "€";

    // Services ---------------------------------------------------------------
    private final ClockifyService clockifyService;
    private final IssueService issueService;
    private final PlanningTable planningTable;
    private final AnalysisTable analysisTable;

    @Autowired
    public AutoDocService(ClockifyService clockifyService, IssueService issueService, PlanningTable planningTable, AnalysisTable analysisTable) {
        this.clockifyService = clockifyService;
        this.issueService = issueService;
        this.planningTable = planningTable;
        this.analysisTable = analysisTable;
    }

    // Methods ----------------------------------------------------------------
    public List<TimeTask> autoDoc(String repoName, String username) {
        Map<Issue, ClockifyTask[]> map = groupIssuesWithHisTime(issueService.findByUsernameAndRepo(username, repoName), clockifyService.getTaskFromWorkspace(repoName, username));
        return map.entrySet().stream().map(entry -> createTimeTask(entry, repoName, username)).toList();
    }

    public Map<Issue, ClockifyTask[]> groupIssuesWithHisTime(Issue[] issues, ClockifyTask[] clockifyTasks) {
        // Hacer que guarde en la base de datos las task.
        return Stream.of(issues).collect(
                Collectors.toMap(issue -> issue, issue -> Stream.of(clockifyTasks)
                        .filter(clockifyTask -> clockifyTask.getDescription().contains(issue.getTitle()))
                        .toArray(ClockifyTask[]::new))
        );
    }

    public TimeTask createTimeTask(Map.Entry<Issue, ClockifyTask[]> entry, String repoName, String username) {
        Issue issue = entry.getKey();
        // Obtener de la base de datos las task por el nombre de la ISSUE.
        ClockifyTask[] clockifyTask = entry.getValue();
        Duration duration = Duration.ZERO;
        Set<RoleStatus> allRoles = new HashSet<>();
        List<User> users = issue.getAssignees().stream().map(issueService::getUserAssignedToIssue).toList();
        // Actualizar el propio usuario.
        List<Employee> employees = users.stream()
                .map(user -> new Employee(user.getFullName(), user.getClockifyId())).toList();
        for (ClockifyTask task : clockifyTask) {
            // La task debe estar relacionado con un usuario.
            Employee employee = findEmployeeClockifyTask(employees, task);
            if (employee == null || task.getTimeInterval().getEnd() == null) // Por si alguien tiene el Clockify arrancado.
                continue;
            if (task.getTagIds() != null) {
                List<RoleStatus> roles = task.getTagIds().stream()
                        .map(tagId -> clockifyService.getRoleFromClockify(repoName, username, tagId)).distinct().toList();
                duration = duration.plus(task.calculateSalary(roles, employee));
                // El usuario debe estar relacionado con todos los posibles roles.
                allRoles.addAll(roles);
            }

        }
        employees.forEach(employee -> System.out.println(employee.getName()));
        return new TimeTask(issue.getBody(), issue.getTitle(), duration, allRoles, employees);
    }

    public Employee findEmployeeClockifyTask(List<Employee> employees, ClockifyTask clockifyTask) {
        return employees.stream().filter(employee -> employee.getClockifyId().equals(clockifyTask.getUserId())).findFirst().orElse(null);
    }

    public List<Employee> getEmployees(List<TimeTask> timeTasks) {
        // Obtenemos todos los usuario de la base de datos.
        List<Employee> employeesTime = timeTasks.stream().flatMap(timeTask -> timeTask.getEmployees().stream()).toList();
        List<String> employeesName = employeesTime.stream().map(Employee::getName).distinct().toList();
        List<Employee> employees = Lists.newArrayList();
        for (String name : employeesName) {
            List<Employee> dataEmployee = employeesTime.stream().filter(employee1 -> employee1.getName().equals(name)).toList();
            for (Employee data : dataEmployee) {
                if (employeesName.contains(data.getName())) {
                    employeesName = employeesName.stream().filter(s -> !s.equals(data.getName())).toList();
                    employees.add(data.getClone());
                } else
                    employees.stream().filter(employee -> employee.getName().equals(data.getName())).forEach(employee -> employee.updateSalary(data));
            }
        }
        return employees;
    }

    public String[] getPlanning(String repoName, String username, String individual, String title) {
        List<TimeTask> timeTasks = autoDoc(repoName, username).stream().filter(task -> task.getEmployees().stream().anyMatch(employee -> employee.getName().equals(individual) && task.getTitle().contains(title))).sorted().toList();
        List<Employee> employees = getEmployees(timeTasks);
        List<Employee> individualEmployee = employees.stream().filter(employee -> employee.getName().equals(individual)).toList();

        // Obtenemos la tabla para las tareas.
        String taskTable = planningTable.getTaskTable(timeTasks).serialize();

        // Obtenemos la tabla para los empleados.
        String personalTable = planningTable.getAllEmployeeTables(individualEmployee);

        // Obtenemos el coste total.
        double cost = Math.round(individualEmployee.stream().mapToDouble(employee -> employee.getSalary().values().stream().mapToDouble(i -> i).sum()).sum() * 100) / (double) 100;

        // Nombres de los empleados.
        String names = planningTable.getNames(employees);

        // Roles del empleado.
        List<RoleStatus> roles = employees.stream().flatMap(employee -> employee.getSalary().keySet().stream()).distinct().toList();
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

    public String[] getPlanning(String repoName, String username, String title) {
        List<TimeTask> timeTasks = autoDoc(repoName, username).stream().filter(task -> task.getTitle().contains(title)).sorted().toList();
        List<Employee> employees = getEmployees(timeTasks);

        // Obtenemos la tabla para las tareas.
        String taskTable = planningTable.getTaskTable(timeTasks).serialize();

        // Obtenemos la tabla para los empleados.
        String personalTable = planningTable.getAllEmployeeTables(employees);

        // Obtenemos el coste total.
        double cost = Math.round(employees.stream().mapToDouble(employee -> employee.getSalary().values().stream().mapToDouble(i -> i).sum()).sum() * 100) / 100.;

        // Nombres de los empleados.
        String names = planningTable.getNames(employees);

        return new String[]{taskTable, personalTable, cost + EURO, names};
    }

    public String getAnalysis(String repoName, String username, String individual, String title) {
        List<TimeTask> timeTasks = autoDoc(repoName, username).stream().filter(task -> task.getEmployees().stream().anyMatch(employee -> employee.getName().equals(individual))
        && task.getTitle().contains(title)).sorted().toList();

        // Obtenemos los enunciados.
        StringBuilder output = analysisTable.getStatements(timeTasks);

        // Obtenemos la tabla con los análisis.
        output.append(analysisTable.getAnalysis(timeTasks));

        return output.toString();
    }

    public String getAnalysis(String repoName, String username, String title) {
        List<TimeTask> timeTasks = autoDoc(repoName, username).stream().filter(task -> task.getTitle().contains(title)).sorted().toList();

        // Obtenemos los enunciados.
        StringBuilder output = analysisTable.getStatements(timeTasks);

        // Obtenemos la tabla con los análisis.
        output.append(analysisTable.getAnalysis(timeTasks));

        return output.toString();
    }
}
