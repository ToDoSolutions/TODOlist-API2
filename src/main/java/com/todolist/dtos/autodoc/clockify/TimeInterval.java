package com.todolist.dtos.autodoc.clockify;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;

import javax.annotation.Generated;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "duration",
        "end",
        "start"
})
@Generated("jsonschema2pojo")
@Getter
public class TimeInterval {

    @JsonProperty("duration")
    private String duration;
    @JsonProperty("end")
    private String end;
    @JsonProperty("start")
    private String start;
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
