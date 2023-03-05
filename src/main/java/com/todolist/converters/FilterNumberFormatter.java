package com.todolist.converters;

import com.todolist.filters.NumberFilter;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class FilterNumberFormatter implements Formatter<NumberFilter> {

    @Override
    public NumberFilter parse(String text, Locale locale) {
        return NumberFilter.parse(text);
    }

    @Override
    public String print(NumberFilter object, Locale locale) {
        return object.toString();
    }
}
