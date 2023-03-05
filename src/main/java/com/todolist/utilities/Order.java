package com.todolist.utilities;

import com.todolist.exceptions.BadRequestException;
import lombok.Getter;
import org.springframework.data.domain.Sort;

import java.util.stream.Stream;

@Getter
public class Order {

    private final Sort.Direction direction;
    private final String field;

    public Order(Character direction, String field) {
        this.direction = direction.equals('+') ? Sort.Direction.ASC : Sort.Direction.DESC;
        this.field = field;
    }

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
