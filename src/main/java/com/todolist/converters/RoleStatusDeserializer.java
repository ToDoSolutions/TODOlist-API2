package com.todolist.converters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.todolist.dtos.autodoc.RoleStatus;

import java.io.IOException;

public class RoleStatusDeserializer extends JsonDeserializer<RoleStatus> {
    @Override
    public RoleStatus deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return RoleStatus.valueOf(p.getText().toUpperCase());
    }
}
