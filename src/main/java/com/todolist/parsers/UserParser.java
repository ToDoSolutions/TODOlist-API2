package com.todolist.parsers;

import com.todolist.entity.User;
import com.todolist.model.ShowUser;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("userParser")
public class UserParser {
    public List<ShowUser> parseList(List<User> users) {
        return users.stream().map(ShowUser::new).collect(Collectors.toList());
    }
}
