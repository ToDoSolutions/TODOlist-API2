package com.todolist.component;

import com.todolist.dtos.autodoc.Area;
import com.todolist.dtos.autodoc.Request;
import com.todolist.entity.Group;
import com.todolist.entity.Role;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.services.RoleService;
import com.todolist.services.autodoc.PlanningService;
import lombok.RequiredArgsConstructor;
import net.steppschuh.markdowngenerator.list.ListBuilder;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.table.TableRow;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PlanningTable {

    // Services ---------------------------------------------------------------
    private final RoleService roleService;
    private final PlanningService planningService;

    // Methods ----------------------------------------------------------------
    public Table getTaskTable(Map<String, List<Task>> timeTasks) {
        List<TableRow> rowTasks = timeTasks.values().stream()
                .map(this::getRow)
                .collect(Collectors.toCollection(() -> new ArrayList<>(timeTasks.size())));
        rowTasks.add(0, new TableRow<>(List.of(HEADER_PLANNING)));
        return new Table.Builder()
                .withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                .withRows(rowTasks)
                .build();
    }
    public static final String MIN = " minutos";

    private String getDistinctNames(List<Task> tasks) {
        return join(tasks.stream()
                .map(task -> task.getUser().getFullName())
                .distinct()
                .toList());
    }

    private String getDistinctRoles(List<Task> tasks) {
        return join(tasks.stream()
                .flatMap(task -> roleService.getStatus(task).stream())
                .map(Role::getName)
                .distinct()
                .toList());
    }

    private Duration calculateTotalDuration(List<Task> tasks) {
        return tasks.stream()
                .map(roleService::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
    }

    private double calculateTotalCost(List<Task> tasks) {
        return tasks.stream()
                .mapToDouble(roleService::getCost)
                .sum();
    }

    private String formatCost(double cost) {
        return String.format(MONEY_REPRESENTATION, cost, EURO);
    }

    public Table getEmployeeTable(User user, Group group, Area area) {
        Map<String, Double> salary = planningService.getTotalCostByRole(user, group, area);
        Table.Builder table = new Table.Builder()
                .withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                .addRow(HEADER_PERSONAL_TABLE);
        salary.forEach((name, cost) -> table.addRow(name, String.format(MONEY_REPRESENTATION, cost, EURO)));
        return table.build();
    }
    public static final String HOURS = " horas y ";

    public String getNames(List<User> employees) {
        ListBuilder listBuilder = new ListBuilder();
        employees.forEach(user -> listBuilder.append(user.getFullName()));
        return listBuilder.toString();
    }

    private String getRolesString(Map<String, List<Task>> taskPerIssue) {
        return join(taskPerIssue.values().stream()
                .flatMap(tasks -> tasks.stream()
                        .flatMap(task -> roleService.findRoleByTaskId(task.getId()).stream()))
                .map(role -> role.getName().toLowerCase())
                .distinct().toList());
    }

    private static String getTime(Duration duration) {
        if (duration.toHours() == NO_HOURS)
            return duration.toMinutes() + MIN;
        return duration.toHours() + HOURS + duration.toMinutes() % 60 + MIN;
    }

    // TODO: Trasladar a FADDA
    private static String join(List<String> list) {
        return list.isEmpty() ? EMPTY : list.size() == 1 ? list.get(FIST_INDEX) :
                String.join(COMMA, list.subList(FIST_INDEX, list.size() - 1)) + AND + list.get(list.size() - 1);
    }

    // Methods -----------------------------------------------------------------
    public String[] createPlanningTable(Request request) throws IOException {
        PlanningService.PlanningData planningData = planningService.getPlanningData(request);

        String taskTable = getTaskTable(planningData.taskPerIssue()).serialize();
        String personalTable = getAllEmployeeTables(planningData.users(), planningData.group(), request.getArea());
        String names = getNames(planningData.users());
        String rolesString = getRolesString(planningData.taskPerIssue());

        return new String[]{taskTable, personalTable, String.format(MONEY_REPRESENTATION, planningData.cost(), EURO), names, rolesString};
    }

    private TableRow<String> getRow(List<Task> tasks) {
        Task firstTask = tasks.get(0);
        String id = firstTask.getIdIssue();
        String title = firstTask.getTitleIssue();
        String namesAsString = getDistinctNames(tasks);
        String rolesAsString = getDistinctRoles(tasks);
        Duration duration = calculateTotalDuration(tasks);
        double cost = calculateTotalCost(tasks);
        String costAsString = formatCost(cost);
        return new TableRow<>(List.of(id, title, namesAsString, rolesAsString, UNKNOWN_AMPLIFICATION, getTime(duration), costAsString));
    }

    public String getAllEmployeeTables(List<User> users, Group group, Area area) {
        StringBuilder personalTable = new StringBuilder();
        users.forEach(user -> personalTable
                .append(JUMP_LINE)
                .append(new Heading(user.getFullName(), SIZE_NAME))
                .append(JUMP_LINE)
                .append(getEmployeeTable(user, group, area).serialize()));
        return personalTable.toString();
    }

    // Constants --------------------------------------------------------------
    protected static final Object[] HEADER_PLANNING = {"Título", "Descripción", "Responsables", "Rol", "Tiempo planificado", "Tiempo real", "Coste"};
    protected static final Object[] HEADER_PERSONAL_TABLE = {"Rol", "Coste"};
    public static final String JUMP_LINE = "\n";
    public static final String EURO = "€"; // No se coloca dentro de MOney Representation por si se quiere cambiar fácilmente.
    public static final String MONEY_REPRESENTATION = "%.2f %s";
    public static final String EMPTY = "";
    public static final int FIST_INDEX = 0;
    public static final int NO_HOURS = 0;
    public static final String COMMA = ", ";
    public static final String AND = " y ";
    public static final int SIZE_NAME = 3;
    public static final String UNKNOWN_AMPLIFICATION = "x";
}
