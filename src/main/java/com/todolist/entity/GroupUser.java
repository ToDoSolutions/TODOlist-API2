package com.todolist.entity;

import javax.persistence.*;

@Table(name = "group_user")
@Entity
public class GroupUser {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id_group_user")
    private long idGroupUser;
    @Column(name = "id_group")
    private long idGroup;
    @Column(name = "id_user")
    private long idUser;

    public GroupUser() {
    }

    public GroupUser(long idGroup, long idUser) {
        this.idGroup = idGroup;
        this.idUser = idUser;
    }

    public GroupUser(Group group, User user) {
        new GroupUser(group.getIdGroup(), user.getIdUser());
    }

    public long getIdGroupUser() {
        return idGroupUser;
    }

    public void setIdGroupUser(long idGroupUser) {
        this.idGroupUser = idGroupUser;
    }

    public long getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(long idGroup) {
        this.idGroup = idGroup;
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }
}
