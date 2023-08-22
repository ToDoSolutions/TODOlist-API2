package com.todolist.dtos;

import com.todolist.entity.Group;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"idGroup"}, callSuper = false)
@NoArgsConstructor
public class ShowGroup extends ShowEntity{

    public static final List<String> ALL_ATTRIBUTES = List.of("idGroup", "name", "description", "createdDate", "users", "numTasks");
    public static final String ALL_ATTRIBUTES_STRING = "idGroup,name,description,createdDate,users,numTasks";
    public static final String COMMA = ",";
    public static final String USERS = "users";
    private Integer idGroup;
    private String name;
    private String description;
    private LocalDate createdDate;

    private String owner;

    private List<ShowUser> users;

    public ShowGroup(Group group, List<ShowUser> users) {
        this.idGroup = group.getId();
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

    @ToJson
    public Map<String, Object> toJson(String fieldsTask, String fieldsUser, String fieldsGroup) {
        Map<String, Object> map = toJson(fieldsGroup, ALL_ATTRIBUTES);
        List<String> attributes = Stream.of(fieldsGroup.split(COMMA)).map(attribute -> attribute.trim().toLowerCase()).toList();
        if (attributes.contains(USERS))
            map.put(USERS, getUsers().stream().map(task -> task.toJson(fieldsTask, fieldsUser)).toList());
        return map;
    }

    public Map<String, Object> toJson() {
        return toJson(ShowTask.ALL_ATTRIBUTES_STRING, ShowUser.ALL_ATTRIBUTES_STRING, ShowGroup.ALL_ATTRIBUTES_STRING);
    }


    public static String getFieldsAsString() {
        return ALL_ATTRIBUTES.toString().replace("[", "").replace("]", "");
    }

}
