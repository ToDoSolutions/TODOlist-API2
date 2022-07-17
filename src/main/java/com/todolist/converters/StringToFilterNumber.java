package com.todolist.converters;

import com.todolist.filters.NumberFilter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToFilterNumber implements Converter<String, NumberFilter> {

    @Override
    public NumberFilter convert(String source) {
        return NumberFilter.parse(source);
    }
}
