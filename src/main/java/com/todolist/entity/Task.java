package com.todolist.entity;

import com.todolist.repository.UserRepository;
import com.todolist.repository.UserTaskRepository;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Table(name = "task")
@Entity
public class Task implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id_task")
    private Long idTask;
    @Column(name = "title")
    @Size(max = 50, message = "The title is too long.")
    @NotNull(message = "The title is required.")
    @NotBlank(message = "The title is required.")
    private String title;
    @Column(name = "description")
    @Size(max = 200, message = "The description is too long.")
    @NotNull(message = "The description is required.")
    @NotBlank(message = "The description is required.")
    private String description;
    @Column(name = "annotation")
    @Size(max = 50, message = "The annotation is too long.")
    private String annotation;
    @Column(name = "status")
    @Pattern(regexp = "DRAFT|draft|IN_PROGRESS|in_progress/IN PROGRESS|in progress|DONE|IN_REVISION|in_revision|IN REVISION|in revision|CANCELLED", message = "The status is invalid.")
    private String status;
    @Column(name = "finished_date")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The finishedDate is invalid.")
    @NotNull(message = "The finishedDate is required.")
    private String finishedDate;
    @Column(name = "start_date")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The startDate is invalid.")
    private String startDate;

    @Column(name = "priority")
    @Max(value = 5, message = "The priority must be between 0 and 5.")
    @Min(value = 0, message = "The priority must be between 0 and 5.")
    private Integer priority;

    @Column(name = "difficulty")
    @Pattern(regexp = "SLEEP|sleep|EASY|easy|MEDIUM|medium|HARD|hard|HARDCORE|hardcore|I_WANT_TO_DIE|i_want_to_die|I WANT TO DIE|i want to die", message = "The difficulty is invalid.")
    private String difficulty;

    public Task() {
        this.idTask = 0L;
    }

    public long getIdTask() {
        return this.idTask;
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

    public List<User> getUsers(UserTaskRepository userTaskRepository, UserRepository userRepository) {
        return userTaskRepository.findByIdTask(this.idTask).stream()
                .map(userTask -> userRepository.findById(userTask.getIdUser()).orElse(null))
                .collect(Collectors.toList());
    }
}
