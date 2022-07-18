package com.todolist.converters;

import com.todolist.filters.DateFilter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToFilterDate implements Converter<String, DateFilter> {
    @Override
    public DateFilter convert(String source) {
        return DateFilter.parse(source);
    }
}
