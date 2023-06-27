package com.todolist.component;

public enum TemplateType {
    ANALYSIS_GROUP("analysis_group"),
    ANALYSIS_INDIVIDUAL("analysis_individual"),
    PLANNING_GROUP("planning_group"),
    PLANNING_INDIVIDUAL("planning_individual");

    private final String fileName;

    TemplateType(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
