package com.todolist.entity.autodoc;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class TimeTask {

    private String description;
    private String conclusion;
    private String decision;
    private Duration duration;
    private Set<Role> roles;
    private double cost;
    private String title;
    private List<Employee> employees;

    public TimeTask(String body, String title, Duration duration, Set<Role> allRoles, List<Employee> employees) {
        if (body != null && body.contains("\r\n\r\n") && body.split("\r\n\r\n").length == 3) {
            String[] text = body.split("\r\n\r\n");
            this.description = text[0];
            this.conclusion = text[1];
            this.decision = text[2];
        } else {
            this.description = body;
        }
        this.title = title;
        this.duration = duration;
        this.roles = allRoles;
        this.employees = employees;
    }

    public double getCost() {
        return employees.stream().mapToDouble(employee -> employee.getSalary().values().stream().mapToDouble(v -> v).sum()).sum();
    }
}
