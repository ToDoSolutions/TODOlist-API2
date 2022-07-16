package com.todolist.converters;

import com.todolist.filters.FilterNumber;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToFilterNumber implements Converter<String, FilterNumber> {

    @Override
    public FilterNumber convert(String source) {
        return FilterNumber.parse(source);
    }
}
