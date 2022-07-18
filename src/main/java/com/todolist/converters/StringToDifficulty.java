package com.todolist.converters;

import com.todolist.dtos.Difficulty;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToDifficulty implements Converter<String, Difficulty> {

    @Override
    public Difficulty convert(String source) {
        return Difficulty.parse(source);
    }
}
