package com.todolist.dtos.autodoc;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"student", "position"}, callSuper = false)
public class TimeTask implements Comparable<TimeTask> {

    private String description;
    private String conclusion;
    private String decision;
    private Duration duration;
    private Set<RoleStatus> roles;
    private double cost;
    private String title;

    private Integer student;
    private Integer position;
    private List<Employee> employees;

    public TimeTask(String body, String title, Duration duration, Set<RoleStatus> allRoles, List<Employee> employees) {
        // System.out.println(body);
        if (body != null && body.contains("\r\n\r\n") && body.split("\r\n\r\n").length == 3) {
            String[] text = body.split("\r\n\r\n");
            this.description = text[0];
            this.conclusion = text[1];
            this.decision = text[2];
        } else {
            this.description = body;
        }
        this.title = title;
        try {
            String id = title.split(":")[0].trim();
            this.student = id.contains("(G)") ? 0 : Integer.parseInt(id.substring(title.indexOf("I") + 1, title.indexOf(")")));
            this.position = (student == 0) ? Integer.parseInt(id.replace("Task", "").replace("(G)", "").trim()) :
                    Integer.parseInt(id.replace("Task", "").replace("(I" + student + ")", "").trim());
        } catch (Exception e) {
            this.title = "Task 0(U): " + this.title;
            this.student = -1;
            this.position = -1;
        }
        this.duration = duration;
        this.roles = allRoles;
        this.employees = employees;
    }

    public double getCost() {
        return employees.stream().mapToDouble(employee -> employee.getSalary().values().stream().mapToDouble(v -> v).sum()).sum();
    }

    public String getID() {
        return title.split(":")[0].trim();
    }

    public String getTask() {
        return title.split(":")[1].trim();
    }

    @Override
    public int compareTo(TimeTask o) {
        if (Objects.equals(student, o.student)) {
            return position.compareTo(o.position);
        } else if (student == 0) {
            return -1;
        } else if (o.student == 0) {
            return 1;
        }
        return student.compareTo(o.student);
    }
}
