package com.todolist.converters;

import com.todolist.dtos.Order;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class OrderFormatter implements Formatter<Order> {


    @Override
    public Order parse(String text, Locale locale) {
        System.out.println(text);
        String field = text.charAt(0) == '+' || text.charAt(0) == '-' ? text.substring(1) : text;
        return new Order(text.charAt(0), field);
    }

    @Override
    public String print(Order object, Locale locale) {
        return object.toString();
    }

}
