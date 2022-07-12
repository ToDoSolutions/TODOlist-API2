package com.todolist.dtos;

import com.todolist.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@ToString
public class ShowUser {
    public static final String ALL_ATTRIBUTES = "idUser,name,surname,email,avatar,bio,location,taskCompleted,tasks";
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

    public Long getTaskCompleted() {
        return getTasks().stream().filter(task -> task.getStatus().equals(Status.DONE)).count();
    }

    public Map<String, Object> getFields(String fieldsUser, String fieldsTask) {
        List<String> attributesShown = Stream.of(fieldsUser.split(",")).map(String::trim).collect(Collectors.toList());
        Map<String, Object> map = new TreeMap<>();
        for (String attribute : attributesShown) {
            if (Objects.equals(attribute.toLowerCase(), "iduser"))
                map.put("idUser", getIdUser());
            else if (Objects.equals(attribute.toLowerCase(), "name"))
                map.put("name", getName());
            else if (Objects.equals(attribute.toLowerCase(), "surname"))
                map.put("surname", getSurname());
            else if (Objects.equals(attribute.toLowerCase(), "email"))
                map.put("email", getEmail());
            else if (Objects.equals(attribute.toLowerCase(), "avatar"))
                map.put("avatar", getAvatar());
            else if (Objects.equals(attribute.toLowerCase(), "bio"))
                map.put("bio", getBio());
            else if (Objects.equals(attribute.toLowerCase(), "location"))
                map.put("location", getLocation());
            else if (Objects.equals(attribute.toLowerCase(), "taskCompleted"))
                map.put("taskcompleted", getTaskCompleted());
            else if (Objects.equals(attribute, "tasks"))
                map.put("tasks", getTasks().stream().map(task -> task.getFields(fieldsTask)).collect(Collectors.toList()));
        }
        return map;
    }
}
