package com.todolist.entity.autodoc.github;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class Label {

    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Object id;
    public String node_id;
    public String url;
    public String name;
    public String color;
    @JsonProperty("default")
    public boolean mydefault;
    public String description;
}
