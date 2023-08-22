package com.todolist.dtos;

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
@EqualsAndHashCode(of = {"idUser"}, callSuper = false)
public class ShowUser extends ShowEntity {
    public static final List<String> ALL_ATTRIBUTES = List.of("id", "name", "surname", "email", "avatar", "bio", "location", "taskCompleted", "tasks");
    public static final String ALL_ATTRIBUTES_STRING = "id,name,surname,email,avatar,bio,location,taskCompleted,tasks";
    public static final String TASKS = "tasks";
    public static final String COMMA = ",";
    private Integer idUser;
    private String name;
    private String surname;
    private String username;
    private String email;
    private String avatar;
    private String bio;
    private String location;
    private List<ShowTask> tasks;


    public ShowUser(User user, List<ShowTask> tasks) {
        this.idUser = user.getId();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.email = user.getEmail();
        this.avatar = user.getAvatar();
        this.bio = user.getBio();
        this.location = user.getLocation();
        this.username = user.getUsername();
        this.tasks = tasks;
    }

    @ToJson
    public Map<String, Object> toJson(String fieldsTask, String fieldsUser) {
        Map<String, Object> map = toJson(fieldsUser, ALL_ATTRIBUTES);
        List<String> attributes = Stream.of(fieldsUser.split(COMMA)).map(attribute -> attribute.trim().toLowerCase()).toList();
        if (attributes.contains(TASKS))
            map.put(TASKS, getTasks().stream().map(task -> task.toJson(fieldsTask)).toList());
        return map;
    }

    @ToJson
    public Map<String, Object> toJson() {
        return toJson(ShowTask.ALL_ATTRIBUTES_STRING, ShowUser.ALL_ATTRIBUTES_STRING);
    }


    public static String getFieldsAsString() {
        return ShowUser.ALL_ATTRIBUTES.toString().replace("[", "").replace("]", "");
    }
}
