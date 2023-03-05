package com.todolist.utilities;

import java.util.function.Predicate;

public class Predicator {

    public static <T> boolean isNullOrValid(T object, Predicate<T> valid) {
        return object == null || valid.test(object);
    }
}
