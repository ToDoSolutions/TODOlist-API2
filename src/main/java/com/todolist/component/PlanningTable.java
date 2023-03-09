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
    public static final String EURO = "€";

    public Table getTaskTable(List<TimeTask> timeTasks) {
        Table.Builder taskTable = new Table.Builder()
                .withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                .addRow(HEADER_PLANNING);
        for (TimeTask timeTask : timeTasks) {
            if (timeTask.getTitle() != null && timeTask.getTitle().contains("9(G)")) {
                System.out.println(timeTask.getTitle());
                System.out.println(timeTask.getDuration().toMinutes());
                timeTask.getEmployees().forEach(employee -> {
                            System.out.println(employee.getName());
                            System.out.println(employee.getSalary());
                        }
                );
                System.out.println(timeTask.getCost());
            }
            taskTable.addRow(timeTask.getTitle().trim(), timeTask.getDescription() != null ? timeTask.getDescription().trim().replace(JUMP_LINE, "") : null, timeTask.getEmployees().stream().map(Employee::getName).reduce((s, s2) -> s + ", " + s2).orElse("")
                    , timeTask.getRoles().stream().map(Role::toString).reduce((s, s2) -> s + ", " + s2).orElse(""), "x"
                    , timeTask.getDuration().toHours() + " horas y " + timeTask.getDuration().toMinutes() % 60 + " minutos"
                    , Math.round(timeTask.getCost() * 100) / 100. + EURO);
        }
        return taskTable.build();
    }

    public Table getEmployeeTable(Employee employee) {
        return new Table.Builder().withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                .addRow("Rol", "Coste")
                .addRow("Desarrollador", employee.getSalaryByRole(Role.DEVELOPER) + EURO)
                .addRow("Analista", employee.getSalaryByRole(Role.ANALYST) + EURO)
                .addRow("Tester", employee.getSalaryByRole(Role.TESTER) + EURO)
                .addRow("Diseñador", employee.getSalaryByRole(Role.MANAGER) + EURO)
                .addRow("Operador", employee.getSalaryByRole(Role.OPERATOR) + EURO)
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
