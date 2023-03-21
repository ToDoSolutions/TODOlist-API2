package com.todolist.dtos.pokemon;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "stats",
        "types"
})
@Generated("jsonschema2pojo")
@Getter
public class Pokemon {

    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();
    @JsonProperty("name")
    private String name;
    @JsonProperty("stats")
    private List<Stat> stats = null;
    @JsonProperty("types")
    private List<Type> types = null;

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
