package com.todolist.entity;

import com.todolist.dtos.Difficulty;
import com.todolist.dtos.Status;
import com.todolist.dtos.autodoc.RoleStatus;
import com.todolist.model.BaseEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"student", "position"}, callSuper = false)
public class Task extends BaseEntity implements Comparable<Task> {

    // Attributes -------------------------------------------------------------
    @NotBlank(message = "The title is required.")
    private String title;

    private Integer student;
    private Integer position;

    @Size(max = 2000, message = "The description is too long.")
    private String description;

    @Size(max = 2000, message = "The conclusion is too long.")
    private String conclusion;

    @Size(max = 2000, message = "The decision is too long.")
    private String decision;

    @Size(max = 50, message = "The annotation is too long.")
    private String annotation;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Max(value = 5, message = "The priority must be between 0 and 5.")
    @Min(value = 0, message = "The priority must be between 0 and 5.")
    private Long priority;


    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    // Derived attributes -----------------------------------------------------
    @Transient
    public String getIdIssue() {
        return title.split(":")[0].trim();
    }

    @Transient
    public String getTitleIssue() {
        return title.split(":")[1].trim();
    }

    // Relationships ----------------------------------------------------------
    @ManyToOne
    private User user;

    // Constructors
    public Task(String title, String body) {
        // TODO: Define in other way.
        if (body != null && body.contains("\r\n\r\n") && body.split("\r\n\r\n").length == 3) {
            String[] text = body.split("\r\n\r\n");
            this.description = text[0];
            this.conclusion = text[1];
            this.decision = text[2];
        } else {
            this.description = body;
        }
        this.title = title;
        if (Pattern.matches("^Task\\s+\\d+\\(\\s\\d+\\):\\s+.+$", title))
            firstWay(title);
        else if (Pattern.matches("^Task \\d+ : \\s#\\d+ - .+$", title))
            secondWay(title);
        else
            throw new IllegalArgumentException("The title is not valid.");
    }

    private void secondWay(String title) {
        if (Pattern.matches("^Task \\d+ : I#\\d+ - .+$", title)) {
            String id = title.split("-")[0].trim();
            this.student = Integer.parseInt(id.split(":")[1].trim().split("#")[1].trim());
            this.position = Integer.parseInt(id.split(":")[0].trim().replace("Task", "").trim());
        } else if (Pattern.matches("^Task \\d+ : G - .+$", title)) {
            String id = title.split("-")[0].trim().split(":")[0].trim();
            this.student = 0;
            this.position = Integer.parseInt(id.replace("Task", "").trim());
        } else {
            this.title = ":V : " + this.title;
            this.student = -1;
            this.position = -1;
        }
    }

    private void firstWay(String title) {
        if (Pattern.matches("^Task\\s+\\d+\\(I\\d+\\):\\s+.+$", title)) {
            String id = title.split(":")[0].trim();
            this.student = Integer.parseInt(id.substring(title.indexOf("I") + 1, title.indexOf(")")));
            this.position = Integer.parseInt(id.replace("Task", "").replace("(I" + student + ")", "").trim());
        } else if (Pattern.matches("^Task\\s+\\d+\\(G\\):\\s+.+$", title)) {
            String id = title.split(":")[0].trim();
            this.student = 0;
            this.position = Integer.parseInt(id.replace("Task", "").replace("(G)", "").trim());
        } else {
            this.title = "Task 0(U): " + this.title;
            this.student = -1;
            this.position = -1;
        }
    }

    // Methods ----------------------------------------------------------------
    @Override
    public int compareTo(Task o) {
        if (Objects.equals(student, o.student)) {
            return position.compareTo(o.position);
        } else if (student == 0) {
            return -1;
        } else if (o.student == 0) {
            return 1;
        }
        return student.compareTo(o.student);
    }
}
