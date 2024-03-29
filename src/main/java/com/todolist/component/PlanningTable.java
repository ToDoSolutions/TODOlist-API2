package com.todolist.component;

import com.todolist.dtos.autodoc.RoleStatus;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.services.RoleService;
import com.todolist.services.UserService;
import net.steppschuh.markdowngenerator.list.ListBuilder;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
public class PlanningTable {

    // Constants --------------------------------------------------------------
    protected static final Object[] HEADER_PLANNING = {"Título", "Descripción", "Responsables", "Rol", "Tiempo planificado", "Tiempo real", "Coste"};
    public static final String JUMP_LINE = "\n";
    public static final String EURO = "€";
    public static final Object[] HEADER_PERSONAL_TABLE = {"Rol", "Coste"};

    // Services ---------------------------------------------------------------
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public PlanningTable(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    // Methods ----------------------------------------------------------------
    public Table getTaskTable(Map<String, List<Task>> timeTasks) {
        Table.Builder taskTable = new Table.Builder()
                .withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                .addRow(HEADER_PLANNING);
        for (Map.Entry<String, List<Task>> entry : timeTasks.entrySet()) {
            String id = entry.getValue().get(0).getIdIssue();
            String title = entry.getValue().get(0).getTitleIssue();
            String names = entry.getValue().stream().map(task -> task.getUser().getFullName()).distinct().reduce((s, s2) -> s + ", " + s2).orElse("");
            String roles = entry.getValue().stream().flatMap(task -> roleService.getStatus(task).stream())
                    .map(RoleStatus::getInSpanish)
                    .reduce((s, s2) -> s + ", " + s2).orElse("");
            Duration duration = entry.getValue().stream().map(roleService::getDuration).reduce(Duration.ZERO, Duration::plus);
            Double cost = Math.round(entry.getValue().stream().mapToDouble(roleService::getCost).sum()*100)/100.;
            taskTable.addRow(id, title, names, roles, "x", duration.toHours() + " horas y " + duration.toMinutes() % 60 + " minutos", cost + EURO);
        }
        return taskTable.build();
    }

    public Table getEmployeeTable(User user, String title) {
        Map<RoleStatus, Double> salary = userService.getCostByTitle(user, title);
        Table.Builder table = new Table.Builder().withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT);
        table.addRow(HEADER_PERSONAL_TABLE);
        for (Map.Entry<RoleStatus, Double> entry: salary.entrySet()) {
            table.addRow(entry.getKey().getInSpanish(), Math.round(entry.getValue()*100)/100. + EURO);
        }
        return table.build();
    }

    public String getAllEmployeeTables(List<User> users, String title) {
        StringBuilder personalTable = new StringBuilder();
        for (User employee : users) {
            personalTable.append(JUMP_LINE).append(new Heading(employee.getFullName(), 3)).append(JUMP_LINE);
            personalTable.append(getEmployeeTable(employee, title).serialize());
        }
        return personalTable.toString();
    }

    public String getNames(List<User> employees) {
        ListBuilder listBuilder = new ListBuilder();
        for (User employee : employees)
            listBuilder.append(employee.getFullName());
        return listBuilder.toString();
    }
}
