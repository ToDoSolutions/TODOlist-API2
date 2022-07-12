package com.todolist.dtos;


import com.todolist.entity.Task;
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

import static java.time.temporal.ChronoUnit.DAYS;

@Getter
@Setter
@ToString
public class ShowTask {

    public static final String ALL_ATTRIBUTES = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration";

    private Long idTask;
    private String title, description, annotation;
    private Status status;
    private LocalDate finishedDate, startDate;
    private Integer priority;
    private Difficulty difficulty;

    public ShowTask(Task task) {
        this.idTask = task.getIdTask();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.annotation = task.getAnnotation();
        this.status = task.getStatus() != null ? Status.parse(task.getStatus()) : null;
        this.finishedDate = LocalDate.parse(task.getFinishedDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.startDate = task.getStartDate() != null ? LocalDate.parse(task.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")) : LocalDate.now();
        this.priority = task.getPriority();
        this.difficulty = task.getDifficulty() != null ? Difficulty.parse(task.getDifficulty()) : null;
    }

    public long getDuration() {
        return DAYS.between(startDate, finishedDate);
    }

    public Map<String, Object> getFields(String fields) {
        List<String> attributes = Stream.of(fields.split(",")).map(String::trim).collect(Collectors.toList());
        Map<String, Object> map = new TreeMap<>();
        for (String attribute : attributes) {
            if (Objects.equals(attribute.toLowerCase(), "idtask"))
                map.put("idTask", getIdTask());
            else if (Objects.equals(attribute.toLowerCase(), "title"))
                map.put("title", getTitle());
            else if (Objects.equals(attribute.toLowerCase(), "description"))
                map.put("description", getDescription());
            else if (Objects.equals(attribute.toLowerCase(), "status"))
                map.put("status", getStatus());
            else if (Objects.equals(attribute.toLowerCase(), "finisheddate"))
                map.put("finishedDate", getFinishedDate());
            else if (Objects.equals(attribute.toLowerCase(), "startdate"))
                map.put("startDate", getStartDate());
            else if (Objects.equals(attribute.toLowerCase(), "annotation"))
                map.put("annotation", getAnnotation());
            else if (Objects.equals(attribute.toLowerCase(), "priority"))
                map.put("priority", getPriority());
            else if (Objects.equals(attribute.toLowerCase(), "difficulty"))
                map.put("difficulty", getDifficulty());
            else if (Objects.equals(attribute.toLowerCase(), "duration"))
                map.put("duration", getDuration());
        }
        return map;
    }
}
