package com.todolist.converters;

import com.todolist.dtos.Status;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToStatus implements Converter<String, Status> {

    @Override
    public Status convert(String source) {
        return Status.parse(source);
    }
}
