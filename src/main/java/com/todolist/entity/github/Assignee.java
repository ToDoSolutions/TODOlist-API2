package com.todolist.entity.github;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

public class Assignee {

    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String login;
    public String avatar_url;
    public String url;
    public String type;
    public boolean site_admin;
}
