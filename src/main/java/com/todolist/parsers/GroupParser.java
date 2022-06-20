package com.todolist.parsers;

import com.todolist.entity.Group;
import com.todolist.model.ShowGroup;
import com.todolist.model.ShowTask;
import com.todolist.model.ShowUser;
import com.todolist.repository.Repositories;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("groupParser")
public class GroupParser {
    public List<ShowGroup> parseList(List<Group> groups, Repositories repositories) {
        return groups.stream().map(group ->
                new ShowGroup(group, repositories.getUsersFromGroup(group).stream().map(user ->
                        new ShowUser(user, repositories.getTasksFromUser(user).stream().map(ShowTask::new)
                                .collect(Collectors.toList()))).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }
}
