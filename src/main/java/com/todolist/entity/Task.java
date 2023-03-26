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

    private String description;
    private String conclusion;
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
    public Double getCost() {
        return roles.stream().mapToDouble(Role::getSalary).sum();
    }

    @Transient
    public String getIdIssue() {
        return title.split(":")[0].trim();
    }

    @Transient
    public String getTitleIssue() {
        return title.split(":")[1].trim();
    }

    @Transient
    public List<RoleStatus> getStatus() {
        return roles.stream().map(Role::getStatus).toList();
    }

    @Transient
    public Optional<Role> getRole(RoleStatus status) {
        if (roles == null)
            return Optional.empty();
        return roles.stream().filter(role -> role.getStatus().equals(status)).findFirst();
    }

    @Transient
    public Duration getDuration() {
        return roles.stream().map(Role::getDuration).reduce(Duration.ZERO, Duration::plus);
    }

    // Relationships ----------------------------------------------------------
    @OneToMany(mappedBy = "task")
    private List<Role> roles;

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
        /**
         * TODO: Implement the regex patterns.
         * Indivual pattern: ^Task\s+\d+\(I\d+\):\s+.+$
         * Group pattern: ^Task\s+\d+\(G\):\s+.+$
         *
         * Other option
         * Individual pattern: ^Task \d+ : I#\d+ - .+$
         * Group pattern: ^Task \d+ : G - .+$
         */
        try {
            String id = title.split(":")[0].trim();
            this.student = id.contains("(G)") ? 0 : Integer.parseInt(id.substring(title.indexOf("I") + 1, title.indexOf(")")));
            this.position = (student == 0) ? Integer.parseInt(id.replace("Task", "").replace("(G)", "").trim()) :
                    Integer.parseInt(id.replace("Task", "").replace("(I" + student + ")", "").trim());
        } catch (Exception e) {
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
