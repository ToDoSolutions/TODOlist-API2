package com.todolist.dtos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.todolist.entity.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"idUser"})
public class ShowUser extends ShowEntity {
    public static final List<String> ALL_ATTRIBUTES = List.of("idUser", "name", "surname", "email", "avatar", "bio", "location", "taskCompleted", "tasks");
    public static final String TASKS = "tasks";
    public static final String COMMA = ",";
    private Long idUser;
    private String name;
    private String surname;
    private String username;
    private String email;
    private String avatar;
    private String bio;
    private String location;
    private List<ShowTask> tasks;


    public ShowUser(User user, List<ShowTask> tasks) {
        this.idUser = user.getIdUser();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.email = user.getEmail();
        this.avatar = user.getAvatar();
        this.bio = user.getBio();
        this.location = user.getLocation();
        this.username = user.getUsername();
        this.tasks = tasks;
    }

    public Map<String, Object> getFields(String fieldsUser, String fieldsTask) {
        Map<String, Object> map = getFields(fieldsUser, ALL_ATTRIBUTES);
        List<String> attributes = Stream.of(fieldsUser.split(COMMA)).map(attribute -> attribute.trim().toLowerCase()).toList();
        if (attributes.contains(TASKS))
            map.put(TASKS, getTasks().stream().map(task -> task.getFields(fieldsTask)).toList());
        return map;
    }

    public Map<String, Object> getFields(String fieldsUser) {
        return getFields(fieldsUser, ShowTask.getFieldsAsString());
    }

    public Map<String, Object> getFields() {
        return getFields(getFieldsAsString(), ShowTask.getFieldsAsString());
    }

    public static String getFieldsAsString() {
        return ShowUser.ALL_ATTRIBUTES.toString().replace("[", "").replace("]", "");
    }
}
