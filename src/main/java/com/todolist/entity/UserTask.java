package com.todolist.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Table(name = "user_task")
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
