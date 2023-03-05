package com.todolist.validators;

import com.todolist.dtos.ShowGroup;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import com.todolist.exceptions.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class FieldValidator {

    public void taskFieldValidate(String fieldsTask) {
        if (!(Arrays.stream(fieldsTask.split(",")).allMatch(field -> ShowTask.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase()))))
            throw new BadRequestException("The tasks' fields are invalid.");
    }

    public void userFieldValidate(String fieldsUser) {
        if (!(Arrays.stream(fieldsUser.split(",")).allMatch(field -> ShowUser.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase()))))
            throw new BadRequestException("The users' fields are invalid.");
    }

    public void groupFieldValidate(String fieldsGroup) {
        if (!(Arrays.stream(fieldsGroup.split(",")).allMatch(field -> ShowGroup.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase()))))
            throw new BadRequestException("The groups' fields are invalid.");
    }
}
