package com.todolist.component;

import com.todolist.dtos.autodoc.RoleStatus;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.services.RoleService;
import com.todolist.services.UserService;
import lombok.AllArgsConstructor;
import net.steppschuh.markdowngenerator.list.ListBuilder;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.table.TableRow;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PlanningTable {

    // Constants --------------------------------------------------------------
    protected static final Object[] HEADER_PLANNING = {"Título", "Descripción", "Responsables", "Rol", "Tiempo planificado", "Tiempo real", "Coste"};
    public static final String JUMP_LINE = "\n";
    public static final String EURO = "€";
    public static final Object[] HEADER_PERSONAL_TABLE = {"Rol", "Coste"};

    // Services ---------------------------------------------------------------
    private final UserService userService;
    private final RoleService roleService;

    private static String getTime(Duration duration) {
        if (duration.toHours() == 0)
            return duration.toMinutes() + " minutos";
        return duration.toHours() + " horas y " + duration.toMinutes() % 60 + " minutos";
    }

    // Methods ----------------------------------------------------------------
    public Table getTaskTable(Map<String, List<Task>> timeTasks) {
        List<TableRow> rowTasks = timeTasks.values().stream()
                .map(this::getRow).toList();
        return new Table.Builder().withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                .addRow(HEADER_PLANNING)
                .withRows(rowTasks).build();
    }

    private TableRow getRow(List<Task> tasks) {
        String id = tasks.get(0).getIdIssue();
        String title = tasks.get(0).getTitleIssue();
        String names = tasks.stream().map(task -> task.getUser().getFullName()).distinct().collect(Collectors.joining(", ", "", " y "));
        String roles = tasks.stream().flatMap(task -> roleService.getStatus(task).stream())
                .map(RoleStatus::getInSpanish).collect(Collectors.joining(", ", "", " y "));
        Duration duration = tasks.stream().map(roleService::getDuration).reduce(Duration.ZERO, Duration::plus);
        Double cost = tasks.stream().mapToDouble(roleService::getCost).sum();
        return new TableRow(List.of(id, title, names, roles, "x", getTime(duration), String.format("%.2f %s", cost, EURO)));
    }

    public Table getEmployeeTable(User user, String title) {
        Map<RoleStatus, Double> salary = userService.getCostByTitle(user, title);
        Table.Builder table = new Table.Builder()
                .withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                .addRow(HEADER_PERSONAL_TABLE);
        salary.forEach((roleStatus, cost) -> table.addRow(roleStatus.getInSpanish(), String.format("%.2f %s", cost, EURO)));
        return table.build();
    }

    public String getAllEmployeeTables(List<User> users, String title) {
        StringBuilder personalTable = new StringBuilder();
        users.forEach(user -> personalTable
                .append(JUMP_LINE)
                .append(new Heading(user.getFullName(), 3))
                .append(getEmployeeTable(user, title).serialize()));
        return personalTable.toString();
    }

    public String getNames(List<User> employees) {
        ListBuilder listBuilder = new ListBuilder();
        employees.forEach(user -> listBuilder.append(user.getFullName()));
        return listBuilder.toString();
    }
}
