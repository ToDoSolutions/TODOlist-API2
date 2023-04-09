package com.todolist.dtos.autodoc;

import com.todolist.entity.Tag;

public enum RoleStatus {
    OPERATOR(20, "Operador"),
    TESTER(20, "Tester"),
    DEVELOPER(20, "Desarrollador"),
    MANAGER(40, "Manager"),
    ANALYST(30, "Analista"),
    UNDEFINED(0, "Desconocido");

    private final int salary;
    private final String spanish;

    public String getInSpanish() {
        return spanish;
    }


    RoleStatus(int i, String spanish) {
        this.salary = i;
        this.spanish = spanish;
    }

    public static RoleStatus parseTag(Tag tag) {
        if (tag.getName() == null)
            return UNDEFINED;
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
