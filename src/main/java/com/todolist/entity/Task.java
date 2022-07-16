package com.todolist.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;

@Table(name = "task")
@Entity
@Getter
@Setter
public class Task implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id_task")
    private Long idTask;

    @Column(name = "title")
    @Size(max = 50, message = "The title is too long.")
    // @NotNull(message = "The title is required.")
    @NotBlank(message = "The title is required.")
    private String title;

    @Column(name = "description")
    @Size(max = 200, message = "The description is too long.")
    // @NotNull(message = "The description is required.")
    @NotBlank(message = "The description is required.")
    private String description;

    @Column(name = "annotation")
    @Size(max = 50, message = "The annotation is too long.")
    private String annotation;

    @Column(name = "status")
    // @Pattern(regexp = "[Dd][Rr][Aa][Ff][Tt]|[Ii][Nn][_ ][Pp][Rr][Oo][Gg][Rr][Ee][Ss][Ss]|[Dd][Oo][Nn][Ee]|[Ii][Nn][_ ][Rr][Ee][Vv][Ii][Ss][Ii][Oo][Nn]|[Cc][Aa][Nn][Cc][Ee][Ll][Ll][Ee][Dd]", message = "The status is invalid.")
    private String status;

    @Column(name = "finished_date")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The finishedDate must be in format yyyy-MM-dd.")
    // @NotNull(message = "The finishedDate is required.")
    private String finishedDate;

    @Column(name = "start_date")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The startDate must be in format yyyy-MM-dd.")
    private String startDate;

    @Column(name = "priority")
    @Max(value = 5, message = "The priority must be between 0 and 5.")
    @Min(value = 0, message = "The priority must be between 0 and 5.")
    private Long priority;

    @Column(name = "difficulty")
    // @Pattern(regexp = "[Ss][Ll][Ee][Ee][Pp]|[Ee][Aa][Ss][Yy]|[Mm][Ee][Dd][Ii][Uu][Mm]|[Hh][Aa][Rr][Dd]|[Hh][Aa][Rr][Dd][Cc][Oo][Rr][Ee]|[Ii][_ ][Ww][Aa][Nn][Tt][_ ][Tt][Oo][_ ][Dd][Ii][Ee]", message = "The difficulty is invalid.")
    private String difficulty;

    public Task() {
        this.idTask = 0L;
    }

    public static Task of(String title, String description, String annotation, String status, String finishedDate, String startDate, Long priority, String difficulty) {
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
}
