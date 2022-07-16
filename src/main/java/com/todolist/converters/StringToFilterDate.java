package com.todolist.converters;

import com.todolist.filters.FilterDate;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToFilterDate implements Converter<String, FilterDate> {
    @Override
    public FilterDate convert(String source) {
        return FilterDate.parse(source);
    }
}
