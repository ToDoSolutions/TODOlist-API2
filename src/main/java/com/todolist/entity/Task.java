package com.todolist.entity;

import com.todolist.dtos.Difficulty;
import com.todolist.dtos.Status;
import com.todolist.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task extends BaseEntity {

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

    // Derived attributes -----------------------------------------------------

    @Transient
    public long getDuration() {
        return DAYS.between(startDate, finishedDate);
    }

    // Relationships ----------------------------------------------------------
    @OneToMany(mappedBy = "task")
    private List<Role> roles;

    @ManyToOne
    private User user;
}
