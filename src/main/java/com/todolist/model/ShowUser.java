package com.todolist.model;

import com.todolist.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ShowUser {
    private long idUser;
    private String name;
    private String surname;
    private String email;
    private String avatar;
    private String bio;
    private String location;
    private List<ShowTask> tasks;

    public ShowUser() {
    }

    public ShowUser(User user) {
        this.idUser = user.getIdUser();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.email = user.getEmail();
        this.avatar = user.getAvatar();
        this.bio = user.getBio();
        this.location = user.getLocation();
        this.tasks = user.getTasks().stream().map(ShowTask::new).collect(Collectors.toList());
    }

    public ShowUser(long idUser, String name, String surname, String email, String avatar, String bio, String location) {
        this.idUser = idUser;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.avatar = avatar;
        this.bio = bio;
        this.location = location;
        this.tasks = new ArrayList<>();
    }

    public ShowUser(long idUser, String name, String surname, String email, String avatar, String bio, String location, List<ShowTask> tasks) {
        this.idUser = idUser;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.avatar = avatar;
        this.bio = bio;
        this.location = location;
        this.tasks = tasks;
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
