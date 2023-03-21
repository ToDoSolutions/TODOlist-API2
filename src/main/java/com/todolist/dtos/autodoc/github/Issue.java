package com.todolist.dtos.autodoc.github;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("jsonschema2pojo")
@Getter
public class Issue {

    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

    private String url;

    @JsonProperty("repository_url")
    private String repositoryUrl;

    @JsonProperty("labels_url")
    private String labelsUrl;

    @JsonProperty("comments_url")
    private String commentsUrl;

    @JsonProperty("events_url")
    private String eventsUrl;

    @JsonProperty("html_url")
    private String htmlUrl;
    private int number;
    private String title;
    private Owner user;
    private String state;
    private boolean locked;
    private Owner assignee;
    private ArrayList<Owner> assignees;
    private int comments;

    @JsonProperty("created_at")
    private Date createdAt;

    @JsonProperty("updated_at")
    private Date updatedAt;

    @JsonProperty("closed_at")
    private Date closedAt;

    private String body;

    @JsonProperty("timeline_url")
    private String timelineUrl;

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
