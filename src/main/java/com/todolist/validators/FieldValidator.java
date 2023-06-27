package com.todolist.validators;

import com.todolist.dtos.ShowGroup;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import com.todolist.exceptions.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class FieldValidator {
    public void taskFieldValidate(String fieldsTask) {
        List<String> lowerAll = ShowTask.ALL_ATTRIBUTES.stream().map(String::toLowerCase).toList();
        System.out.println("fieldsTask = " + fieldsTask);
        System.out.println("lowerAll = " + lowerAll);
        if (!(Arrays.stream(fieldsTask.split(",")).allMatch(field -> lowerAll.contains(field.toLowerCase()))))
            throw new BadRequestException("The tasks' fields are invalid.");
    }

    public void userFieldValidate(String fieldsUser) {
        List<String> lowerAll = ShowUser.ALL_ATTRIBUTES.stream().map(String::toLowerCase).toList();
        if (!(Arrays.stream(fieldsUser.split(",")).allMatch(field -> lowerAll.contains(field.toLowerCase()))))
            throw new BadRequestException("The users' fields are invalid.");
    }

    public void groupFieldValidate(String fieldsGroup) {
        List<String> lowerAll = ShowGroup.ALL_ATTRIBUTES.stream().map(String::toLowerCase).toList();
        if (!(Arrays.stream(fieldsGroup.split(",")).allMatch(field -> lowerAll.contains(field.toLowerCase()))))
            throw new BadRequestException("The groups' fields are invalid.");
    }
}
