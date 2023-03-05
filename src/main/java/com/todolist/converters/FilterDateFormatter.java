package com.todolist.converters;

import com.todolist.filters.DateFilter;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class FilterDateFormatter implements Formatter<DateFilter> {
    @Override
    public DateFilter parse(String text, Locale locale) {
        return DateFilter.parse(text);
    }

    @Override
    public String print(DateFilter object, Locale locale) {
        return object.toString();
    }
}
