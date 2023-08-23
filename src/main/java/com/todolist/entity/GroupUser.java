package com.todolist.entity;

import com.todolist.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.todolist.model.NamedEntity;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupUser extends NamedEntity {

    // Attributes -------------------------------------------------------------
    private Integer idGroup;

    private Integer idUser;
}
