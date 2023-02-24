package com.todolist.entity.autodoc;

import com.todolist.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class TimeTask {

    private String description;

    private String decision;
    private double duration;
    private Set<Role> roles;
    private double cost;
    private String title;
    private List<Employee> employees;

    public TimeTask(String body, String title, double duration,Set<Role> allRoles, List<Employee> employees) {
        this.description = body;
        this.title = title;
        this.duration = duration;
        this.roles = allRoles;
        this.employees = employees;
    }

    public double getCost() {
        return employees.stream().mapToDouble(employee -> employee.getSalary().values().stream().mapToDouble(v -> v).sum()).sum();
    }
}
