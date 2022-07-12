package com.todolist.entity.github;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "auto_init",
        "private",
        "gitignore_template",
})
@Generated("jsonschema2pojo")
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
    private String isAutoInit;
    @JsonProperty("private")
    @Pattern(regexp = "^(true|false)$", message = "The private is invalid.")
    private String isPrivate;
    @JsonProperty("gitignore_template")
    @Size(max = 100, message = "The gitignore_template is too long.")
    private String gitignoreTemplate;
    @JsonProperty("is_template")
    @Pattern(regexp = "^(true|false)$", message = "The is_template is invalid.")
    private String isTemplate;
    @JsonProperty("homepage")
    @Size(max = 255, message = "The homepage is too long.")
    private String homepage;

    @JsonProperty("name")
    @NotNull()
    @NotBlank

    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("auto_init")
    public String getAutoInit() {
        return isAutoInit;
    }

    @JsonProperty("auto_init")
    public void setAutoInit(String isAutoInit) {
        this.isAutoInit = isAutoInit;
    }

    @JsonProperty("private")
    public String getPrivate() {
        return isPrivate;
    }

    @JsonProperty("private")
    public void setPrivate(String isPrivate) {
        this.isPrivate = isPrivate;
    }

    @JsonProperty("gitignore_template")
    public String getGitignoreTemplate() {
        return gitignoreTemplate;
    }

    @JsonProperty("gitignore_template")
    public void setGitignoreTemplate(String gitignoreTemplate) {
        this.gitignoreTemplate = gitignoreTemplate;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("is_template")
    public String getTemplate() {
        return isTemplate;
    }

    @JsonProperty("is_template")
    public void setTemplate(String isTemplate) {
        this.isTemplate = isTemplate;
    }

    @JsonProperty("homepage")
    public String getHomepage() {
        return homepage;
    }

    @JsonProperty("homepage")
    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
