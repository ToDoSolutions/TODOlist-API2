package com.todolist.model;

import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.repository.Repositories;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ShowUser {
    public static final String ALL_ATTRIBUTES = "idUser,name,surname,email,avatar,bio,location,taskCompleted,tasks";
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

    public ShowUser(User user, List<ShowTask> tasks) {
        this.idUser = user.getIdUser();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.email = user.getEmail();
        this.avatar = user.getAvatar();
        this.bio = user.getBio();
        this.location = user.getLocation();
        this.tasks = tasks;
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

    public List<ShowTask> getTasks() {
        return tasks;
    }

    public Long getTaskCompleted() {
        return getTasks().stream().filter(task -> task.getStatus().equals(Status.DONE)).count();
    }

    public Map<String, Object> getFields(String fieldsUser, String fieldsTask) {
        List<String> attributesShown = Stream.of(fieldsUser.split(",")).map(String::trim).collect(Collectors.toList());
        Map<String, Object> map = new TreeMap<>();
        for (String attribute : attributesShown) {
            if (Objects.equals(attribute, "idUser"))
                map.put(attribute, getIdUser());
            else if (Objects.equals(attribute, "name"))
                map.put(attribute, getName());
            else if (Objects.equals(attribute, "surname"))
                map.put(attribute, getSurname());
            else if (Objects.equals(attribute, "email"))
                map.put(attribute, getEmail());
            else if (Objects.equals(attribute, "avatar"))
                map.put(attribute, getAvatar());
            else if (Objects.equals(attribute, "bio"))
                map.put(attribute, getBio());
            else if (Objects.equals(attribute, "location"))
                map.put(attribute, getLocation());
            else if (Objects.equals(attribute, "taskCompleted"))
                map.put(attribute, getTaskCompleted());
            else if (Objects.equals(attribute, "tasks"))
                map.put(attribute, getTasks().stream().map(task -> task.getFields(fieldsTask)).collect(Collectors.toList()));
        }
        return map;
    }

}
