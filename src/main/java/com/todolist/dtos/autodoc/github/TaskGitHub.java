package com.todolist.dtos.autodoc.github;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "description",
        "releases_url",
        "created_at",
        "clone_url",
})
@Generated("jsonschema2pojo")
@Getter
public class TaskGitHub {

    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;

    @JsonProperty("releases_url")
    private String releasesUrl;

    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("clone_url")
    private String cloneUrl;

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
