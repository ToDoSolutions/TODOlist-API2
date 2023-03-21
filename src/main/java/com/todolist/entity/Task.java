package com.todolist.entity;

import com.todolist.dtos.Difficulty;
import com.todolist.dtos.Status;
import com.todolist.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task extends BaseEntity implements Serializable {

    // Attributes -------------------------------------------------------------
    @Size(max = 50, message = "The title is too long.")
    @NotBlank(message = "The title is required.")
    private String title;

    @Size(max = 200, message = "The description is too long.")
    @NotBlank(message = "The description is required.")
    private String description;

    @Size(max = 50, message = "The annotation is too long.")
    private String annotation;

    @Enumerated(EnumType.STRING)
    private Status status;

    @NotNull(message = "The finishedDate is required.")
    private LocalDate finishedDate;

    private LocalDate startDate;

    @Max(value = 5, message = "The priority must be between 0 and 5.")
    @Min(value = 0, message = "The priority must be between 0 and 5.")
    private Long priority;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;


    public long getDuration() {
        return DAYS.between(startDate, finishedDate);
    }
}
