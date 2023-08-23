package com.todolist.dtos;


import com.todolist.entity.Task;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"idTask"}, callSuper = false)
public class ShowTask extends ShowEntity {

    public static final List<String> ALL_ATTRIBUTES = List.of("id", "title", "description", "status", "finishedDate", "startDate", "annotation", "priority", "difficulty", "duration");
    public static final String ALL_ATTRIBUTES_STRING = "id,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration";

    private Integer idTask;
    private String title, description, annotation;
    private LocalDate finishedDate, startDate;
    private Long priority;
    private Difficulty difficulty;

    public ShowTask(Task task) {
        this.idTask = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.annotation = task.getAnnotation();
        this.priority = task.getPriority();
        this.difficulty = task.getDifficulty();
    }

    @ToJson
    public Map<String, Object> toJson(String fields) {
        return toJson(fields, ALL_ATTRIBUTES);
    }


    public Map<String, Object> toJson() {
        return toJson(ShowTask.ALL_ATTRIBUTES_STRING);
    }


    public static String getFieldsAsString() {
        return ALL_ATTRIBUTES.toString().replace("[", "").replace("]", "");
    }
}
