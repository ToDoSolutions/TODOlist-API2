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
    @ManyToOne
    private Group group;

    @Transient
    public void addDuration(LocalDateTime start, LocalDateTime end) {
        duration = duration.plus(Duration.between(start, end));
    }

    // Derived attributes -----------------------------------------------------
    @Transient
    public Double getSalary() {
        return getFinalSalary(duration.toMinutes() / 60.);
    }

    // Relationships ----------------------------------------------------------
    @ManyToOne
    private Task task;

    public double getFinalSalary(double hours) {
        return hours * salary;
    }
}
