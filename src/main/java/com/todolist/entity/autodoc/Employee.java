package com.todolist.entity.autodoc;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(of = {"name", "clockifyId"})
public class Employee {
    private String name;
    private Map<Role, Double> salary;
    private String clockifyId;

    public Employee(String name, String clockifyId) {
        this.name = name;
        this.clockifyId = clockifyId;
        this.salary = new HashMap<>();
    }

    public void keepSalary(Role role, Duration duration) {
        double hours = duration.toMinutes() / 60.;
        if (salary.containsKey(role))
            salary.put(role, salary.get(role) + role.getFinalSalary(hours));
        else
            salary.put(role, role.getFinalSalary(hours));
    }

    public void updateSalary(Employee employee) {

        for (Role role : employee.getSalary().keySet())
            if (salary.containsKey(role))
                salary.put(role, salary.get(role) + employee.getSalary().get(role));
            else
                salary.put(role, employee.getSalary().get(role));
    }

    public double getSalaryByRole(Role role) {
        if (salary == null || !salary.containsKey(role))
            return 0;
        return Math.round(salary.get(role)*100)/100.;

    }
}
