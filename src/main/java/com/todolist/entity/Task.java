package com.todolist.entity;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Table(name = "task")
@Entity
public class Task implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idTask")
    private Long idTask;
    @Column(name = "title")
    @Size(max = 50, message = "The title is too long.")
    private String title;
    @Column(name = "description")
    @Size(max = 200, message = "The description is too long.")
    private String description;
    @Column(name = "annotation")
    @Size(max = 50, message = "The annotation is too long.")
    private String annotation;
    @Column(name = "status")
    @Pattern(regexp = "DRAFT|IN_PROGRESS|DONE|IN_REVISION|CANCELLED", message = "The status is invalid.")
    private String status;
    @Column(name = "finishedDate")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The finishedDate is invalid.")
    private String finishedDate;
    @Column(name = "startDate")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The startDate is invalid.")
    private String startDate;

    @Column(name = "priority")
    @Max(value = 5, message = "The priority must be between 0 and 5.")
    @Min(value = 0, message = "The priority must be between 0 and 5.")
    private Integer priority;

    @Column(name = "difficulty")
    @Pattern(regexp = "SLEEP|EASY|MEDIUM|HARD|HARDCORE|I_WANT_TO_DIE", message = "The difficulty is invalid.")
    private String difficulty;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_task", joinColumns = @JoinColumn(name = "idUser"), inverseJoinColumns = @JoinColumn(name = "idTask"))
    private List<User> users;


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

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
