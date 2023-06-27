package com.todolist.filters;

import com.todolist.exceptions.BadRequestException;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record NumberFilter(Boolean isGreater, Boolean isLess, Boolean isEqual, Long number) {

    public static NumberFilter parse(String filterWithNumber) {
        Pattern pattern = Pattern.compile("([<>=]{1,2})(\\d+)");
        Matcher matcher = pattern.matcher(filterWithNumber);
        if (matcher.matches()) {
            String filter = matcher.group(1);
            Long number = Long.parseLong(matcher.group(2));
            return switch (filter) {
                case "<" -> new NumberFilter(false, true, false, number);
                case ">" -> new NumberFilter(true, false, false, number);
                case "=", "==" -> new NumberFilter(false, false, true, number);
                case "<=", "=<" -> new NumberFilter(false, true, true, number);
                case ">=", "=>" -> new NumberFilter(true, false, true, number);
                case "!=", "<>", "><" -> new NumberFilter(true, true, false, number);
                default ->
                        throw new BadRequestException("The filter is not valid and it should have a parameter filter (< - > - = - <= - =< - >= - => - == - != - <> - ><) and a number without decimals.");
            };
        } else {
            throw new BadRequestException("The filter is not valid and it should have a parameter filter (< - > - = - <= - =< - >= - => - == - != - <> - ><) and a number without decimals.");
        }
    }

    public boolean isValid(Long newNumber) {
        return newNumber != null && (
                (isGreater && newNumber > number) ||
                        (isLess && newNumber < number) ||
                        (isEqual && Objects.equals(newNumber, number))
        );
    }
}