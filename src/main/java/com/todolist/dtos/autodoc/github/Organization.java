package com.todolist.dtos.autodoc.github;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "login",
        "description",
        "created_at"
})
@Generated("jsonschema2pojo")
@Getter
public class Organization {

    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();
    @JsonProperty("login")
    private String login;
    @JsonProperty("description")
    private String description;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

