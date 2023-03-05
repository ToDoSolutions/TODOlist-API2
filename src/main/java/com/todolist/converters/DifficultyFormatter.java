package com.todolist.converters;

import com.todolist.dtos.Difficulty;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Locale;

@Component
public class DifficultyFormatter implements Formatter<Difficulty> {

    @Override
    public Difficulty parse(String text, Locale locale) throws ParseException {
        return Difficulty.parse(text);
    }

    @Override
    public String print(Difficulty object, Locale locale) {
        return object.toString();
    }
}
