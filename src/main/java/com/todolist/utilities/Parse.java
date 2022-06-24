package com.todolist.utilities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Parse {

    public static LocalDate date(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
