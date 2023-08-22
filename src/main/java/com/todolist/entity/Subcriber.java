package com.todolist.entity;

import com.todolist.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Getter
@Setter
@Entity
public class Subcriber extends BaseEntity {

    @ManyToOne
    private User user;

    @ManyToOne
    private Group group;

}
