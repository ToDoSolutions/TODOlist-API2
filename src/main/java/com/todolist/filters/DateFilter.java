package com.todolist.filters;

import com.todolist.exceptions.BadRequestException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public record DateFilter(boolean isGreater, boolean isLess, boolean isEqual, LocalDate date) {

    public static DateFilter parse(String filterWithDate) {
        Pattern pattern = Pattern.compile("([<>=]{1,2})(\\d{4}-\\d{2}-\\d{2})");
        Matcher matcher = pattern.matcher(filterWithDate);
        if (matcher.matches()) {
            String filter = matcher.group(1);
            LocalDate localDate = LocalDate.parse(matcher.group(2), DateTimeFormatter.ISO_DATE);
            return switch (filter) {
                case "<" -> new DateFilter(false, true, false, localDate);
                case ">" -> new DateFilter(true, false, false, localDate);
                case "=", "==" -> new DateFilter(false, false, true, localDate);
                case "<=", "=<" -> new DateFilter(false, true, true, localDate);
                case ">=", "=>" -> new DateFilter(true, false, true, localDate);
                case "!=", "<>", "><" -> new DateFilter(true, true, false, localDate);
                default ->
                        throw new BadRequestException("The filter is not valid and it should have a parameter filter (< - > - = - <= - =< - >= - => - == - != - <> - ><) and a date with the format YYYY-MM-DD.");
            };
        } else {
            throw new BadRequestException("The filter is not valid and it should have a parameter filter (< - > - = - <= - =< - >= - => - == - != - <> - ><) and a date with the format YYYY-MM-DD.");
        }
    }

    public boolean isValid(LocalDate newDate) {
        return newDate != null && (
                (isGreater && newDate.isAfter(date)) ||
                        (isLess && newDate.isBefore(date)) ||
                        (isEqual && newDate.isEqual(date))
        );
    }
}
