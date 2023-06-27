package com.todolist.entity;

import com.todolist.dtos.Difficulty;
import com.todolist.dtos.Status;
import com.todolist.exceptions.BadRequestException;
import com.todolist.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;
import java.util.regex.Pattern;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
        parseBody(body);
        this.title = title;
        parseTitle(title);
    }

    private void parseBody(String body) {
        String jump = "\r\n\r\n";
        if (body != null && body.contains(jump) && body.split(jump).length == 3) {
            String[] text = body.split(jump);
            this.description = text[0];
            this.conclusion = text[1];
            this.decision = text[2];
        } else {
            this.description = body;
        }
    }

    private void parseTitle(String title) {
        if (isFirstWay(title))
            parseFirstWay(title);
        else if (isSecondWay(title))
            parseSecondWay(title);
        else
            throw new BadRequestException("The title is not valid. It must follow the pattern: 'Task <number>(<student>): <title>', 'Task <number> : I#<student> - <title>', or 'Task <number> : G - <title>'.");
    }

    private boolean isFirstWay(String title) {
        return Pattern.compile("^Task\\s\\d+\\((I\\d+|G?)\\):\\s+").split(title).length == 2;
    }

    private void parseFirstWay(String title) {
        if (Pattern.compile("^Task\\s+\\d+\\(I\\d+\\):\\s+").split(title).length == 2) {
            String id = title.split(":")[0].trim();
            this.student = Integer.parseInt(id.substring(title.indexOf("I") + 1, title.indexOf(")")));
            this.position = Integer.parseInt(id.replace("Task", "").replace("(I" + student + ")", "").trim());
        } else if (Pattern.compile("^Task\\s+\\d+\\(G\\):\\s+").split(title).length == 2) {
            String id = title.split(":")[0].trim();
            this.student = 0;
            this.position = Integer.parseInt(id.replace("Task", "").replace("(G)", "").trim());
        } else {
            this.title = "Task 0(U): " + this.title;
            this.student = -1;
            this.position = -1;
        }
    }

    private boolean isSecondWay(String title) {
        return Pattern.compile("^Task \\d+ : \\s#\\d+ -\\s+").split(title).length == 2;
    }

    private void parseSecondWay(String title) {
        if (Pattern.compile("^Task \\d+ : I#\\d+ -\\s+").split(title).length == 2) {
            String id = title.split("-")[0].trim();
            this.student = Integer.parseInt(id.split(":")[1].trim().split("#")[1].trim());
            this.position = Integer.parseInt(id.split(":")[0].trim().replace("Task", "").trim());
        } else if (Pattern.compile("^Task \\d+ : G - .+$").split(title).length == 2) {
            String id = title.split("-")[0].trim().split(":")[0].trim();
            this.student = 0;
            this.position = Integer.parseInt(id.replace("Task", "").trim());
        } else {
            this.title = ":V : " + this.title;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Task task = (Task) o;
        return getId() != null && Objects.equals(getId(), task.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
