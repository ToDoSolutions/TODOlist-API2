package com.todolist.entity.autodoc;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TimeTask {

    private String description;
    private double duration;
    private String title;
    private List<String> usernames;
}
