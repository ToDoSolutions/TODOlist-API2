package com.todolist.component;

import com.todolist.dtos.autodoc.Employee;
import com.todolist.dtos.autodoc.RoleStatus;
import com.todolist.dtos.autodoc.TimeTask;
import net.steppschuh.markdowngenerator.list.ListBuilder;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlanningTable {

    // Constants --------------------------------------------------------------
    protected static final Object[] HEADER_PLANNING = {"Título", "Descripción", "Responsables", "Rol", "Tiempo planificado", "Tiempo real", "Coste"};
    public static final String JUMP_LINE = "\n";
    public static final String EURO = "€";

    // Methods ----------------------------------------------------------------
    public Table getTaskTable(List<TimeTask> timeTasks) {
        Table.Builder taskTable = new Table.Builder()
                .withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                .addRow(HEADER_PLANNING);
        for (TimeTask timeTask : timeTasks) {
            taskTable.addRow(
                    timeTask.getID(),
                    timeTask.getTask(),
                    timeTask.getEmployees().stream().map(Employee::getName).reduce((s, s2) -> s + ", " + s2).orElse(""),
                    timeTask.getRoles().stream().map(RoleStatus::toString).reduce((s, s2) -> s + ", " + s2).orElse(""), "x",
                    timeTask.getDuration().toHours() + " horas y " + timeTask.getDuration().toMinutes() % 60 + " minutos",
                    Math.round(timeTask.getCost() * 100) / 100. + EURO);
        }
        return taskTable.build();
    }

    public Table getEmployeeTable(Employee employee) {
        return new Table.Builder().withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                .addRow("Rol", "Coste")
                .addRow("Desarrollador", employee.getSalaryByRole(RoleStatus.DEVELOPER) + EURO)
                .addRow("Analista", employee.getSalaryByRole(RoleStatus.ANALYST) + EURO)
                .addRow("Tester", employee.getSalaryByRole(RoleStatus.TESTER) + EURO)
                .addRow("Diseñador", employee.getSalaryByRole(RoleStatus.MANAGER) + EURO)
                .addRow("Operador", employee.getSalaryByRole(RoleStatus.OPERATOR) + EURO)
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

    public String getNames(List<Employee> employees) {
        ListBuilder listBuilder = new ListBuilder();
        for (Employee employee : employees)
            listBuilder.append(employee.getName());
        return listBuilder.toString();
    }
}
