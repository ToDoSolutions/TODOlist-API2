package com.todolist.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class UserTask {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long idUserTask;

    private Long idTask;

    private Long idUser;

    public UserTask() {
    }

    public UserTask(long idUser, long idTask) {
        this.idUserTask = 0L;
        this.idTask = idTask;
        this.idUser = idUser;
    }
}
