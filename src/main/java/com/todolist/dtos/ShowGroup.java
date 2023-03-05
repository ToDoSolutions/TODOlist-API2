package com.todolist.dtos;

import com.todolist.entity.Group;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"idGroup"}, callSuper = false)
public class ShowGroup extends ShowEntity{

    public static final List<String> ALL_ATTRIBUTES = List.of("idGroup", "name", "description", "createdDate", "users", "numTasks");
    public static final String ALL_ATTRIBUTES_STRING = "idGroup,name,description,createdDate,users,numTasks";
    public static final String COMMA = ",";
    public static final String USERS = "users";
    private Long idGroup;
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
        this.createdDate = group.getCreatedDate();
        this.users = users;
    }

    public Long getNumTasks() {
        return (long) users.stream()
                .flatMap(user -> user.getTasks().stream().map(ShowTask::getIdTask))
                .collect(Collectors.toSet()).size();
    }

    public Map<String, Object> toJson(String fieldsGroup, String fieldsUser, String fieldsTask) {
        Map<String, Object> map = toJson(fieldsGroup, ALL_ATTRIBUTES);
        List<String> attributes = Stream.of(fieldsGroup.split(COMMA)).map(attribute -> attribute.trim().toLowerCase()).toList();
        if (attributes.contains(USERS))
            map.put(USERS, getUsers().stream().map(task -> task.toJson(fieldsUser, fieldsTask)).toList());
        return map;
    }

    public Map<String, Object> toJson(String fieldsGroup, String fieldsUser) {
        return toJson(fieldsGroup, fieldsUser, ShowTask.ALL_ATTRIBUTES.toString());
    }

    public Map<String, Object> toJson(String fieldsGroup) {
        return toJson(fieldsGroup, ShowUser.getFieldsAsString(), ShowTask.getFieldsAsString());
    }

    public Map<String, Object> toJson() {
        return toJson(getFieldsAsString(), ShowUser.getFieldsAsString(), ShowTask.getFieldsAsString());
    }

    public static String getFieldsAsString() {
        return ALL_ATTRIBUTES.toString().replace("[", "").replace("]", "");
    }

}
