package com.todolist.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class GroupUser {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long idGroupUser;

    private Long idGroup;

    private Long idUser;

    public GroupUser(Long idGroup, Long idUser) {
        this.idGroup = idGroup;
        this.idUser = idUser;
    }
}
