package com.todolist.dtos;

import com.todolist.entity.Group;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@ToString
public class ShowGroup {

    public static final String ALL_ATTRIBUTES = "idGroup,name,description,createdDate,users,numTasks";
    private long idGroup;
    private String name;
    private String description;
    private LocalDate createdDate;

    private String owner;

    private List<ShowUser> users;

    public ShowGroup(Group group, List<ShowUser> users) {
        this.idGroup = group.getIdGroup();
        this.name = group.getName();
        this.description = group.getDescription();
        this.createdDate = group.getCreatedDate() != null ? LocalDate.parse(group.getCreatedDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")) : LocalDate.now();
        this.users = users;
    }

    public Integer getNumTasks() {
        return users.stream()
                .flatMap(user -> user.getTasks().stream().map(ShowTask::getIdTask))
                .collect(Collectors.toSet()).size();
    }

    public Map<String, Object> getFields(String fieldsGroup, String fieldsUser, String fieldsTask) {
        List<String> attributesShown = Stream.of(fieldsGroup.split(",")).map(String::trim).toList();
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
}
