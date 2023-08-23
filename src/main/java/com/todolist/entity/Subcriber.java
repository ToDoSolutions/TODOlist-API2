package com.todolist.entity;

import com.todolist.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Entity
public class Subcriber extends BaseEntity {

    @ManyToOne
    private User user;

    @ManyToOne
    private Group group;

}
