package com.todolist.entity;

import com.todolist.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SaveData(entityName = "group_user", fileName = "group_user.csv")
public class GroupUser extends BaseEntity {

    // Attributes -------------------------------------------------------------
    private Integer idGroup;

    private Integer idUser;
}
