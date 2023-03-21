package com.todolist.dtos.autodoc;

import com.todolist.entity.Tag;

public enum RoleStatus {
    OPERATOR(20),
    TESTER(20),
    DEVELOPER(20),
    MANAGER(40),
    ANALYST(30),
    UNDEFINED(0);

    private final int salary;


    RoleStatus(int i) {
        this.salary = i;
    }

    public static RoleStatus parseTag(Tag tag) {
        String name = tag.getName().toUpperCase();
        return switch (name) {
            case "OPERATOR" -> OPERATOR;
            case "TESTER" -> TESTER;
            case "DEVELOPER" -> DEVELOPER;
            case "MANAGER" -> MANAGER;
            case "ANALYST" -> ANALYST;
            default -> UNDEFINED;
        };
    }

    public double getFinalSalary(double hours) {
        return hours * salary;
    }
}
