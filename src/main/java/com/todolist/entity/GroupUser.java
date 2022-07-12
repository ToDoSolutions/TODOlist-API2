package com.todolist.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Table(name = "group_user")
@Entity
@Getter
@Setter
public class GroupUser {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id_group_user")
    private Long idGroupUser;
    @Column(name = "id_group")
    private Long idGroup;
    @Column(name = "id_user")
    private Long idUser;

    public GroupUser() {

    }
}
