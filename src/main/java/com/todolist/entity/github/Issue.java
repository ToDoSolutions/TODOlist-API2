package com.todolist.entity.github;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("jsonschema2pojo")
public class Issue {

    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String url;

    @JsonProperty("repository_url")
    public String repositoryUrl;

    @JsonProperty("labels_url")
    public String labelsUrl;

    @JsonProperty("comments_url")
    public String commentsUrl;

    @JsonProperty("events_url")
    public String eventsUrl;

    @JsonProperty("html_url")
    public String htmlUrl;
    public int number;
    public String title;
    public Owner user;
    public ArrayList<Label> labels;
    public String state;
    public boolean locked;
    public Owner assignee;
    public ArrayList<Owner> assignees;
    public int comments;

    @JsonProperty("created_at")
    public Date createdAt;

    @JsonProperty("updated_at")
    public Date updatedAt;

    @JsonProperty("closed_at")
    public Date closedAt;

    public String body;

    @JsonProperty("timeline_url")
    public String timelineUrl;
}
