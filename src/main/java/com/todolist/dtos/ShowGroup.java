package com.todolist.dtos;

import com.todolist.entity.Group;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ShowGroup {

    public static final String ALL_ATTRIBUTES = "idGroup,name,description,createdDate,users,numTasks";
    private long idGroup;
    private String name;
    private String description;
    private LocalDate createdDate;

    private String owner;

    private List<ShowUser> users;

    public ShowGroup() {
    }

    public ShowGroup(Group group, List<ShowUser> users) {
        this.idGroup = group.getIdGroup();
        this.name = group.getName();
        this.description = group.getDescription();
        this.createdDate = group.getCreatedDate() != null ? LocalDate.parse(group.getCreatedDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")) : LocalDate.now();
        this.users = users;
    }

    public ShowGroup(long idGroup, String name, String description, LocalDate createdDate) {
        this.idGroup = idGroup;
        this.name = name;
        this.description = description;
        this.createdDate = createdDate;
        this.users = new ArrayList<>();
    }

    public long getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(long idGroup) {
        this.idGroup = idGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public List<ShowUser> getUsers() {
        return users;
    }

    public Integer getNumTasks() {
        return users.stream()
                .flatMap(user -> user.getTasks().stream().map(ShowTask::getIdTask))
                .collect(Collectors.toSet()).size();
    }

    public Map<String, Object> getFields(String fieldsGroup, String fieldsUser, String fieldsTask) {
        List<String> attributesShown = Stream.of(fieldsGroup.split(",")).map(String::trim).collect(Collectors.toList());
        Map<String, Object> map = new TreeMap<>();
        for (String attribute : attributesShown) {
            if (Objects.equals(attribute.toLowerCase(), "idgroup"))
                map.put("idGroup", getIdGroup());
            else if (Objects.equals(attribute.toLowerCase(), "name"))
                map.put("name", getName());
            else if (Objects.equals(attribute.toLowerCase(), "description"))
                map.put("description", getDescription());
            else if (Objects.equals(attribute.toLowerCase(), "createddate"))
                map.put("createdDate", getCreatedDate());
            else if (Objects.equals(attribute.toLowerCase(), "numtasks"))
                map.put("numTasks", getNumTasks());
            else if (Objects.equals(attribute.toLowerCase(), "users"))
                map.put("users", users.stream().map(u -> u.getFields(fieldsUser, fieldsTask)).collect(Collectors.toList()));
        }
        return map;
    }

    @Override
    public String toString() {
        return "ShowGroup{" +
                "idGroup=" + idGroup +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdDate=" + createdDate +
                ", users=" + users +
                '}';
    }
}
