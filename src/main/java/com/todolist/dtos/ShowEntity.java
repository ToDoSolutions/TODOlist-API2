package com.todolist.dtos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import java.util.List;
import java.util.Map;

public abstract class ShowEntity {

    protected Map<String, Object> toJson(String fields, List<String> allAttributes) {

        List<String> attributesNotNeeded = allAttributes.stream().map(String::trim).filter(attribute -> !fields.contains(attribute.toLowerCase())).toList();
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        Map<String, Object> map = mapper.convertValue(this, Map.class);
        for (String attribute : attributesNotNeeded) map.remove(attribute);
        return map;
    }
}
