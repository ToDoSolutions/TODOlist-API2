package com.todolist.validators.user;

import com.todolist.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;
import java.util.regex.Pattern;

@Component
public class GitHubUserAuthenticatedValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        if (user.getToken() == null)
            errors.rejectValue("token", "Token is required.");
        if (Pattern.compile("ghp_[a-zA-Z0-9]{36}").matcher(user.getToken()).find())
            errors.rejectValue("token", "The token is invalid.");
    }

    public void validateGitHubUserAuthenticated(String password, Object target, Errors errors) {
        User user = (User) target;
        if (Objects.equals(password, user.getPassword()))
            errors.rejectValue("password", "The password must be different from the current password.");
        validate(user, errors);
    }
}
