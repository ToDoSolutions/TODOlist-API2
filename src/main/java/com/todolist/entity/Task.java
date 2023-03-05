package com.todolist.entity;

import com.todolist.dtos.Difficulty;
import com.todolist.dtos.Status;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"idTask"})
public class Task implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long idTask;

    @Size(max = 50, message = "The title is too long.")
    @NotBlank(message = "The title is required.")
    @NotNull(message = "The title is required.")
    private String title;

    @Size(max = 200, message = "The description is too long.")
    @NotBlank(message = "The description is required.")
    @NotNull(message = "The description is required.")
    private String description;

    @Size(max = 50, message = "The annotation is too long.")
    private String annotation;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The finishedDate must be in format yyyy-MM-dd.")
    @NotBlank(message = "The finishedDate is required.")
    @NotNull(message = "The finishedDate is required.")
    private LocalDate finishedDate;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The startDate must be in format yyyy-MM-dd.")
    private LocalDate startDate;

    @Max(value = 5, message = "The priority must be between 0 and 5.")
    @Min(value = 0, message = "The priority must be between 0 and 5.")
    private Long priority;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    private String workSpaceId;

    public Task() {
        this.idTask = 0L;
    }

    public static Task of(String title, String description, String annotation, Status status, LocalDate finishedDate, LocalDate startDate, Long priority, Difficulty difficulty) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setAnnotation(annotation);
        task.setStatus(status);
        task.setFinishedDate(finishedDate);
        task.setStartDate(startDate);
        task.setPriority(priority);
        task.setDifficulty(difficulty);
        return task;
    }

    public long getDuration() {
        return DAYS.between(startDate, finishedDate);
    }
}
