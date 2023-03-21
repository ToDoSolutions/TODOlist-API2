package com.todolist.dtos.autodoc.github;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Generated;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "description",
        "auto_init",
        "private",
        "gitignore_template",
        "is_template",
        "homepage"
})
@Generated("jsonschema2pojo")
@Getter
@Setter
public class Repo {

    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();
    @JsonProperty("name")
    @Size(max = 100, message = "The name is too long.")
    private String name;
    @JsonProperty("description")
    @Size(max = 255, message = "The description is too long.")
    private String description;
    @JsonProperty("auto_init")
    @Pattern(regexp = "^(true|false)$", message = "The auto_init is invalid.")
    private Boolean isAutoInit;
    @JsonProperty("private")
    @Pattern(regexp = "^(true|false)$", message = "The private is invalid.")
    private Boolean isPrivate;
    @JsonProperty("gitignore_template")
    @Size(max = 100, message = "The gitignore_template is too long.")
    private String gitignoreTemplate;
    @JsonProperty("is_template")
    @Pattern(regexp = "^(true|false)$", message = "The is_template is invalid.")
    private Boolean isTemplate;
    @JsonProperty("homepage")
    @Size(max = 255, message = "The homepage is too long.")
    private String homepage;

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
