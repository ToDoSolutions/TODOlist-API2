package com.todolist.utilities;

public class Predicate {

    public static <T> boolean isNullOrValid(T object, Boolean valid) {
        return object == null || valid;
    }
}
