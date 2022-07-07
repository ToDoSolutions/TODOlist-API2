package com.todolist.parsers;


import com.todolist.entity.Task;
import com.todolist.dtos.ShowTask;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("taskParser")
public class TaskParser {
    public List<ShowTask> parseList(List<Task> tasks) {
        return tasks.stream().map(ShowTask::new).collect(Collectors.toList());
    }
}
