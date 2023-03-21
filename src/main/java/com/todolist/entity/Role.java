package com.todolist.entity;

import com.todolist.model.NamedEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Entity
public class Role extends NamedEntity {

    // Attributes -------------------------------------------------------------
    private String amount;

    // Relationships ----------------------------------------------------------
    @ManyToOne
    private Task task;
}
