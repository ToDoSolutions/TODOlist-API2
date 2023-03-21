package com.todolist.component;

import com.todolist.dtos.Difficulty;
import com.todolist.dtos.autodoc.github.TaskGitHub;
import com.todolist.entity.Group;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class GitHubConverter {

    /* REPO */
    public Group turnTaskGitHubIntoTask(TaskGitHub taskGitHub, LocalDate finishedDate, Long priority, Difficulty difficulty) {
        Group group = new Group(taskGitHub.getDescription(), LocalDate.parse(taskGitHub.getCreatedAt().split("T")[0]), null);
        group.setName(taskGitHub.getName());
        return group;
    }
}
