package com.todolist.entity.autodoc;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class TimeTask {

    private String description;

    private String decision;
    private double duration;
    private Set<Role> roles;
    private double cost;
    private String title;
    private List<String> usernames;
}
