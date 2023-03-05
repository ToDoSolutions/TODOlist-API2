package com.todolist.validators.task;

import com.todolist.entity.Task;
import com.todolist.exceptions.BadRequestException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

@Component
public class DateTaskValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Task.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Task task = (Task) target;
        if (!task.getStartDate().isBefore(task.getFinishedDate()))
            errors.rejectValue("startDate", "The startDate must be before the finishedDate.");
        if (task.getFinishedDate().isBefore(LocalDate.now()))
            errors.rejectValue("finishedDate", "The finishedDate must be after the current date.");
    }
}
