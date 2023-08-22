package com.todolist.entity;

import com.todolist.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Transient;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Role extends BaseEntity {

    // Attributes -------------------------------------------------------------
    private String tagName;
    private String name;
    private Double salary;
    private Duration duration;

    // Relationships ----------------------------------------------------------
    @ManyToOne
    private Group group;

    // Constructors -----------------------------------------------------------
    public Role(String tagName, Task task) {
        super();
        duration = Duration.ZERO;
        String[] data = tagName.split("-");
        name = data[0];
        salary = Double.parseDouble(data[1]);
        this.tagName = tagName;
        this.task = task;
    }


    // Derived attributes -----------------------------------------------------
    @Transient
    public Double getSalary() {
        return getFinalSalary(duration.toMinutes() / 60.);
    }

    public double getFinalSalary(double hours) {
        return hours * salary;
    }

    // Relationships ----------------------------------------------------------
    @ManyToOne
    private Task task;


    // Auxiliary methods ------------------------------------------------------
    @Transient
    public void addDuration(LocalDateTime start, LocalDateTime end) {
        duration = duration.plus(Duration.between(start, end));
    }
}
