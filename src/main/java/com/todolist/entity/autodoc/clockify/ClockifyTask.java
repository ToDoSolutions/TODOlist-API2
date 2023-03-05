package com.todolist.entity.autodoc.clockify;

import com.fasterxml.jackson.annotation.*;
import com.todolist.entity.autodoc.Employee;
import com.todolist.entity.autodoc.Role;

import javax.annotation.Generated;
import javax.validation.Valid;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
public class ClockifyTask {
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
    @JsonIgnore
    @Valid
    private final Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("billable")
    public String getBillable() {
        return billable;
    }

    @JsonProperty("billable")
    public void setBillable(String billable) {
        this.billable = billable;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("isLocked")
    public String getIsLocked() {
        return isLocked;
    }

    @JsonProperty("isLocked")
    public void setIsLocked(String isLocked) {
        this.isLocked = isLocked;
    }

    @JsonProperty("projectId")
    public String getProjectId() {
        return projectId;
    }

    @JsonProperty("projectId")
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @JsonProperty("tagIds")
    public List<String> getTagIds() {
        return tagIds;
    }

    @JsonProperty("tagIds")
    public void setTagIds(List<String> tagIds) {
        this.tagIds = tagIds;
    }

    @JsonProperty("taskId")
    public String getTaskId() {
        return taskId;
    }

    @JsonProperty("taskId")
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @JsonProperty("timeInterval")
    public TimeInterval getTimeInterval() {
        return timeInterval;
    }

    @JsonProperty("timeInterval")
    public void setTimeInterval(TimeInterval timeInterval) {
        this.timeInterval = timeInterval;
    }

    @JsonProperty("userId")
    public String getUserId() {
        return userId;
    }

    @JsonProperty("userId")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @JsonProperty("workspaceId")
    public String getWorkspaceId() {
        return workspaceId;
    }

    @JsonProperty("workspaceId")
    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Duration calculateSalary(List<Role> roles, Duration duration, Employee employee) {
        LocalDateTime start = LocalDateTime.parse(getTimeInterval().getStart(), DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime end = LocalDateTime.parse(getTimeInterval().getEnd(), DateTimeFormatter.ISO_DATE_TIME);
        Duration difference = Duration.between(start, end);
        roles.forEach(role -> employee.keepSalary(role, difference));
        return difference;
    }
}
