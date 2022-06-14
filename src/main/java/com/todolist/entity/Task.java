package com.todolist.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;

@Table(name="NOTA")
@Entity
public class Task implements Serializable {

    @GeneratedValue
    @Id
    @Column(name = "idTask")
    private long idTask;
    @Column(name = "title")
    @Max(value = 50, message = "The title is too long")
    private String title;
    @Column(name = "description")
    @Max(value = 200, message = "The description is too long")
    private String description;
    @Column(name = "annotation")
    @Max(value = 50, message = "The annotation is too long")
    private String annotation;
    @Column(name = "status")
    @Pattern(regexp = "DRAFT|IN_PROGRESS|DONE|IN_REVISION|CANCELLED")
    private String status;
    @Column(name = "finishedDate")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The finished date is not valid, it should be yyyy-MM-dd format")
    private String finishedDate;
    @Column(name = "startDate")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The start date is not valid, it should be yyyy-MM-dd format")
    private String startDate;

    @Column(name = "priority")
    @Size(max=5, message = "The priority of the task is not valid, it must be a number between 0 and 5.")
    private Integer priority;

    @Column(name = "difficulty")
    private String difficulty;

    public Task() {
    }

    public Task(long idTask, String title, String description, String annotation, String status, String finishedDate, String startDate, Integer priority, String difficulty) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFinishedDate() {
        return finishedDate;
    }

    public void setFinishedDate(String finishedDate) {
        this.finishedDate = finishedDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}
