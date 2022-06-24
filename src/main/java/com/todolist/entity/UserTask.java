package com.todolist.entity;

import javax.persistence.*;

@Table(name = "user_task")
@Entity
public class UserTask {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id_user_task")
    private long idUserTask;
    @Column(name = "id_task")
    private long idTask;
    @Column(name = "id_user")
    private long idUser;

    public UserTask() {
    }

    public UserTask(long idTask, long idUser) {
        this.idTask = idTask;
        this.idUser = idUser;
    }

    public UserTask(User user, Task task) {
        new UserTask(task.getIdTask(), user.getIdUser());
    }

    public long getIdUserTask() {
        return idUserTask;
    }

    public void setIdUserTask(long idUserTask) {
        this.idUserTask = idUserTask;
    }

    public long getIdTask() {
        return idTask;
    }

    public void setIdTask(long idTask) {
        this.idTask = idTask;
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }
}
