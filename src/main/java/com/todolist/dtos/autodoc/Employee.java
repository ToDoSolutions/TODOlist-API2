package com.todolist.dtos.autodoc;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(of = {"name", "clockifyId"})
@ToString
public class Employee {
    private String name;
    private Map<RoleStatus, Double> salary;
    private String clockifyId;

    public Employee(String name, String clockifyId) {
        this.name = name;
        this.clockifyId = clockifyId;
        this.salary = new HashMap<>();
    }

    public void keepSalary(RoleStatus role, Duration duration) {
        double hours = duration.toMinutes() / 60.;
        if (salary.containsKey(role))
            salary.put(role, salary.get(role) + role.getFinalSalary(hours));
        else
            salary.put(role, role.getFinalSalary(hours));
    }

    public void updateSalary(Employee employee) {

        for (RoleStatus role : employee.getSalary().keySet())
            if (salary.containsKey(role))
                salary.put(role, salary.get(role) + employee.getSalary().get(role));
            else
                salary.put(role, employee.getSalary().get(role));
    }

    public double getSalaryByRole(RoleStatus role) {
        if (salary == null || !salary.containsKey(role))
            return 0;
        return Math.round(salary.get(role) * 100) / 100.;
    }

    public Employee getClone() {
        Employee employee = new Employee(name, clockifyId);
        employee.setSalary(new HashMap<>(salary));
        return employee;
    }
}
