package com.todolist.dtos;

import com.todolist.exceptions.BadRequestException;
import lombok.Getter;
import org.springframework.data.domain.Sort;

import java.util.stream.Stream;

@Getter
public class Order {

    // Attributes -------------------------------------------------------------
    private final Sort.Direction direction;
    private final String field;

    // Constructors -----------------------------------------------------------
    public Order(Character direction, String field) {
        this.direction = direction.equals('-') ? Sort.Direction.DESC : Sort.Direction.ASC;
        this.field = field.trim();
    }

    // Methods ----------------------------------------------------------------
    @Override
    public String toString() {
        return direction.equals(Sort.Direction.ASC) ? "+" + field : "-" + field;
    }

    public void validateOrder(String fields) {
        String[] listFields = fields.split(",");
        if (Stream.of(listFields).noneMatch(prop -> prop.equalsIgnoreCase(field)))
            throw new BadRequestException("The order is invalid.");
    }

    public Sort getSort() {
        return Sort.by(direction, field);
    }
}
