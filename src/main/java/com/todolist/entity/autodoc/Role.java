package com.todolist.entity.autodoc;

import com.todolist.entity.autodoc.clockify.Tag;

public enum Role {
    OPERATOR(20),
    TESTER(20),
    DEVELOPER(20),
    MANAGER(40),
    ANALYST(30);

    private int salary;


    Role(int i) {
        this.salary = i;
    }

    public double getFinalSalary(double hours) {
        return hours * salary;
    }

    public static Role parseTag(Tag tag) {
        String name = tag.getName().toUpperCase();
        return switch (name) {
            case "OPERATOR" -> OPERATOR;
            case "TESTER" -> TESTER;
            case "DEVELOPER" -> DEVELOPER;
            case "MANAGER" -> MANAGER;
            case "ANALYST" -> ANALYST;
            default -> throw new IllegalStateException("Unexpected value: " + name);
        };
    }
}
