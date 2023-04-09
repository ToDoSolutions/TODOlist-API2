package com.todolist.dtos.autodoc.clockify;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;

import javax.annotation.Generated;
import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "billable",
        "description",
        "id",
        "isLocked",
        "projectId",
        "tagIds",
        "taskId",
        "timeInterval",
        "userId",
        "workspaceId"
})
@Generated("jsonschema2pojo")
@Getter
public class ClockifyTask {
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    @JsonProperty("billable")
    private String billable;
    @JsonProperty("description")
    private String description;
    @JsonProperty("id")
    private String id;
    @JsonProperty("isLocked")
    private String isLocked;
    @JsonProperty("projectId")
    private String projectId;
    @JsonProperty("tagIds")
    @Valid
    private List<String> tagIds;
    @JsonProperty("taskId")
    private String taskId;
    @JsonProperty("timeInterval")
    @Valid
    private TimeInterval timeInterval;
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("workspaceId")
    private String workspaceId;

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }


}
