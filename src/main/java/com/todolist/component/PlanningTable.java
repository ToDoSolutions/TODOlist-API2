package com.todolist.component;

import com.todolist.entity.autodoc.Employee;
import com.todolist.entity.autodoc.Role;
import com.todolist.entity.autodoc.TimeTask;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlanningTable {

    public static final Object[] HEADER_PLANNING = {"Título", "Descripción", "Responsables", "Rol", "Tiempo planificado", "Tiempo real", "Coste"};
    public static final String JUMP_LINE = "\n";

    public Table getTaskTable(List<TimeTask> timeTasks) {
        Table.Builder taskTable = new Table.Builder()
                .withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                .addRow(HEADER_PLANNING);
        for (TimeTask timeTask : timeTasks) {
            taskTable.addRow(timeTask.getTitle().trim(), timeTask.getDescription() != null ? timeTask.getDescription().trim().replace(JUMP_LINE, "") : null, timeTask.getEmployees().stream().map(Employee::getName).reduce((s, s2) -> s + ", " + s2).orElse("")
                    , timeTask.getRoles().stream().map(Role::toString).reduce((s, s2) -> s + ", " + s2).orElse(""), "x"
                    , timeTask.getDuration().toHours() + " horas y " + timeTask.getDuration().toMinutes() % 60 + " minutos"
                    , Math.round(timeTask.getCost() * 100) / 100. + "€");
        }
        return taskTable.build();
    }

    public Table getEmployeeTable(Employee employee) {
        return new Table.Builder().withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                .addRow("Rol", "Coste")
                .addRow("Desarrollador", employee.getSalaryByRole(Role.DEVELOPER) + "€")
                .addRow("Analista", employee.getSalaryByRole(Role.ANALYST) + "€")
                .addRow("Tester", employee.getSalaryByRole(Role.TESTER) + "€")
                .addRow("Diseñador", employee.getSalaryByRole(Role.MANAGER) + "€")
                .addRow("Operador", employee.getSalaryByRole(Role.OPERATOR) + "€")
                .build();
    }

    public String getAllEmployeeTables(List<Employee> employees) {
        StringBuilder personalTable = new StringBuilder();
        for (Employee employee : employees) {
            personalTable.append(JUMP_LINE).append(new Heading(employee.getName(), 3)).append(JUMP_LINE);
            personalTable.append(getEmployeeTable(employee).serialize());
        }
        return personalTable.toString();
    }

}
