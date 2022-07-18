package com.todolist.filters;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.regex.Pattern;

@Getter
@AllArgsConstructor
public class NumberFilter {

    private Boolean isGreater;
    private Boolean isLess;
    private Boolean isEqual;
    private Long number;

    public static NumberFilter parse(String filterWithNumber) {
        // [<>=]{2}\d+|[<>=]\d+
        if (Pattern.compile("[<>=]\\d+").matcher(filterWithNumber).matches()) {
            String filter = filterWithNumber.charAt(0) + "";
            Long number = Long.parseLong(filterWithNumber.substring(1));
            return switch (filter) {
                case "<" -> new NumberFilter(false, true, false, number);
                case ">" -> new NumberFilter(true, false, false, number);
                case "=" -> new NumberFilter(false, false, true, number);
                default ->
                        throw new IllegalArgumentException("The filter is not valid and it should have a parameter filter (<,>,=,<=,=<,>=,=>,==,!=,<>,><) and a number without decimals.");
            };
        } else if (Pattern.compile("[<>=]{2}\\d+").matcher(filterWithNumber).matches()) {
            String filter = filterWithNumber.charAt(0) + "" + filterWithNumber.charAt(1);
            Long number = Long.parseLong(filterWithNumber.substring(2));
            return switch (filter) {
                case "<=", "=<" -> new NumberFilter(false, true, true, number);
                case ">=", "=>" -> new NumberFilter(true, false, true, number);
                case "==" -> new NumberFilter(false, false, true, number);
                case "!=", "<>", "><" -> new NumberFilter(true, true, false, number);
                default ->
                        throw new IllegalArgumentException("The filter is not valid and it should have a parameter filter (<,>,=,<=,=<,>=,=>,==,!=,<>,><) and a number without decimals.");
            };
        } else {
            throw new IllegalArgumentException("The filter is not valid and it should have a parameter filter (<,>,=,<=,=<,>=,=>,==,!=,<>,><) and a number without decimals.");
        }
    }

    public boolean isValid(Long number) {
        if (Objects.isNull(number))
            return false;
        else if (Boolean.TRUE.equals(isGreater)) // newNumber > number
            return number > this.number;
        else if (Boolean.TRUE.equals(isLess)) // newNumber < number
            return number < this.number;
        else if (Boolean.TRUE.equals(isEqual)) // newNumber == number
            return Objects.equals(number, this.number);
        return false;
    }
}
