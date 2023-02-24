package com.todolist.entity.autodoc;

import com.todolist.entity.autodoc.Role;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

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
    }

    public void keepSalary(Role role, double hours) {
        if (salary == null)
            salary = new HashMap<>();
        if (salary.containsKey("role"))
            salary.put(role, salary.get(role) + role.getFinalSalary(hours));
        else
            salary.put(role, role.getFinalSalary(hours));
    }

    public void updateSalary(Employee employee) {
        if (salary == null)
            salary = new HashMap<>();
        if (employee.getSalary() != null)
            for (Role role : employee.getSalary().keySet())
                if (salary.containsKey(role))
                    salary.put(role, salary.get(role) + employee.getSalary().get(role));
                else
                    salary.put(role, employee.getSalary().get(role));
    }

    public double getSalaryByRole(Role role) {
        if (salary == null || !salary.containsKey(role))
            return 0;
        return salary.get(role);

    }
}
