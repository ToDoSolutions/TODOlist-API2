package com.todolist.parsers;

import com.todolist.entity.Group;
import com.todolist.model.ShowGroup;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("groupParser")
public class GroupParser {
    public List<ShowGroup> parseList(List<Group> groups) {
        return groups.stream().map(ShowGroup::new).collect(Collectors.toList());
    }
}
