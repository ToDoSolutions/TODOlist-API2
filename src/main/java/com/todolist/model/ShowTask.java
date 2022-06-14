package com.todolist.model;


import com.todolist.entity.Task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ShowTask {

    private long idTask;
    private String title, description, annotation;
    private Status status;
    private LocalDate finishedDate, startDate;
    private Integer priority;
    private Difficulty difficulty;

    public ShowTask() {
    }

    public ShowTask(Task task) {
        this.idTask = task.getIdTask();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.annotation = task.getAnnotation();
        this.status = Status.parse(task.getStatus());
        this.finishedDate = LocalDate.parse(task.getFinishedDate(), DateTimeFormatter.ofPattern("\\d{4}-\\d{2}-\\d{2}"));
        this.startDate = LocalDate.parse(task.getStartDate(), DateTimeFormatter.ofPattern("\\d{4}-\\d{2}-\\d{2}"));
        this.priority = task.getPriority();
        this.difficulty = Difficulty.parse(task.getDifficulty());
    }

    public ShowTask(long idTask, String title, String description, String annotation, Status status, LocalDate finishedDate, LocalDate startDate, Integer priority, Difficulty difficulty) {
        this.idTask = idTask;
        this.title = title;
        this.description = description;
        this.annotation = annotation;
        this.status = status;
        this.finishedDate = finishedDate;
        this.startDate = startDate;
        this.priority = priority;
        this.difficulty = difficulty;
    }

    public long getIdTask() {
        return idTask;
    }

    public void setIdTask(long idTask) {
        this.idTask = idTask;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getFinishedDate() {
        return finishedDate;
    }

    public void setFinishedDate(LocalDate finishedDate) {
        this.finishedDate = finishedDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}
