package com.todolist.parsers;

import com.todolist.entity.User;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import com.todolist.repository.Repositories;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("userParser")
public class UserParser {
    public List<ShowUser> parseList(List<User> users, Repositories repositories) {
        return users.stream().map(user -> new ShowUser(user, repositories.getTasksFromUser(user).stream().map(ShowTask::new).collect(Collectors.toList()))).collect(Collectors.toList());
    }
}
