package com.todolist.dtos;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.todolist.entity.Task;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"idTask"})
public class ShowTask extends ShowEntity{

    public static final List<String> ALL_ATTRIBUTES = List.of("idTask","title","description","status","finishedDate","startDate","annotation","priority","difficulty","duration");

    private Long idTask;
    private String title, description, annotation;
    private Status status;
    private LocalDate finishedDate, startDate;
    private Long priority;
    private Difficulty difficulty;

    public ShowTask() {
    }

    public ShowTask(Task task) {
        this.idTask = task.getIdTask();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.annotation = task.getAnnotation();
        this.status = task.getStatus();
        this.finishedDate = task.getFinishedDate();
        this.startDate = task.getStartDate();
        this.priority = task.getPriority();
        this.difficulty = task.getDifficulty();
    }

    public Map<String, Object> getFields(String fields) {
        return getFields(fields, ALL_ATTRIBUTES);
    }

    public Map<String, Object> getFields() {
        return getFields(getFieldsAsString());
    }

    public static String getFieldsAsString() {
        return ALL_ATTRIBUTES.toString().replace("[", "").replace("]", "");
    }
}
