package com.todolist.entity;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
public class Tag {

    @Id
    private String id;

    private String name;

    private String workspaceId;

    private Boolean archived;
}
