package com.todolist.model;


import com.todolist.entity.Task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.DAYS;

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
        this.status = task.getStatus() != null ? Status.valueOf(task.getStatus().toUpperCase()): null;
        this.finishedDate = task.getFinishedDate() != null ? LocalDate.parse(task.getFinishedDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")): null;
        this.startDate = task.getStartDate() != null ? LocalDate.parse(task.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")): null;
        this.priority = task.getPriority();
        this.difficulty = task.getDifficulty() != null ? Difficulty.valueOf(task.getDifficulty().toUpperCase()): null;
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

    public long getDuration() {
        return DAYS.between(startDate, finishedDate);
    }

    public Map<String, Object> getFields(String fields) {
        List<String> attributes = Stream.of(fields.split(",")).map(String::trim).collect(Collectors.toList());
        Map<String, Object> map = new TreeMap<>();
        for (String attribute : attributes) {
            if (Objects.equals(attribute, "idTask"))
                map.put(attribute, getIdTask());
            else if (Objects.equals(attribute, "title"))
                map.put(attribute, getTitle());
            else if (Objects.equals(attribute, "description"))
                map.put(attribute, getDescription());
            else if (Objects.equals(attribute, "status"))
                map.put(attribute, getStatus());
            else if (Objects.equals(attribute, "finishedDate"))
                map.put(attribute, getFinishedDate());
            else if (Objects.equals(attribute, "startDate"))
                map.put(attribute, getStartDate());
            else if (Objects.equals(attribute, "annotation"))
                map.put(attribute, getAnnotation());
            else if (Objects.equals(attribute, "priority"))
                map.put(attribute, getPriority());
            else if (Objects.equals(attribute, "difficulty"))
                map.put(attribute, getDifficulty());
            else if (Objects.equals(attribute, "duration"))
                map.put(attribute, getDuration());
        }
        return map;
    }
}
