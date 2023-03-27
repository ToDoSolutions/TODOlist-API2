package com.todolist.entity;

import com.todolist.dtos.autodoc.RoleStatus;
import com.todolist.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Transient;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Role extends BaseEntity {

    // Attributes -------------------------------------------------------------
    @Enumerated
    private RoleStatus status;

    private Duration duration;

    // Derived attributes -----------------------------------------------------
    @Transient
    public Double getSalary() {
        return status.getFinalSalary(duration.toMinutes() / 60.);
    }

    @Transient
    public void addDuration(LocalDateTime start, LocalDateTime end) {
        duration = duration.plus(Duration.between(start, end));
    }

    // Relationships ----------------------------------------------------------
    @ManyToOne
    private Task task;
}
