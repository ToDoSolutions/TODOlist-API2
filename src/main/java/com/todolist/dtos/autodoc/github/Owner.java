package com.todolist.dtos.autodoc.github;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "login",
        "avatar_url",
        "name",
        "email",
        "location",
        "bio",
})
@Generated("jsonschema2pojo")
@Getter
public class Owner {

    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();
    @JsonProperty("login")
    private String login;
    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("email")
    private String email;

    @JsonProperty("bio")
    private String bio;

    @JsonProperty("location")
    private String location;

    @JsonProperty("name")
    private String name;

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
