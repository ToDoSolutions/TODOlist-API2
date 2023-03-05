package com.todolist.converters;

import com.todolist.dtos.Status;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class StatusFormatter implements Formatter<Status> {

    @Override
    public Status parse(String text, Locale locale) {
        return Status.parse(text);
    }

    @Override
    public String print(Status object, Locale locale) {
        return object.toString();
    }
}
