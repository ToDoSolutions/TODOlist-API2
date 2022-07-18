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
    @Column(name = "id_user_task")
    private Long idUserTask;
    @Column(name = "id_task")
    private Long idTask;
    @Column(name = "id_user")
    private Long idUser;

    public UserTask() {
    }

    public UserTask(long idTask, long idUser) {
        this.idUserTask = 0L;
        this.idTask = idTask;
        this.idUser = idUser;
    }
}
