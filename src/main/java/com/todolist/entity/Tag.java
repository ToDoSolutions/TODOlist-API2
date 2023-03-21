package com.todolist.entity;

import com.todolist.model.NamedEntity;
import lombok.Getter;

import javax.persistence.Entity;

@Entity
@Getter
public class Tag extends NamedEntity {

    private String clockifyId;

    private String workspaceId;

    private Boolean archived;
}
