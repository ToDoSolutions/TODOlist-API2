package com.todolist.services;

import com.todolist.entity.User;
import com.todolist.entity.autodoc.TimeTask;
import com.todolist.entity.autodoc.clockify.ClockifyTask;
import com.todolist.entity.autodoc.github.Issue;
import com.todolist.entity.autodoc.github.Owner;
import com.todolist.services.github.IssueService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AutoDocService {

    private final ClockifyService clockifyService;
    private final IssueService issueService;
    private final UserService userService;

    public AutoDocService(ClockifyService clockifyService, IssueService issueService, UserService userService) {
        this.clockifyService = clockifyService;
        this.issueService = issueService;
        this.userService = userService;
    }

    public List<TimeTask> autoDoc(String repoName, String username) {
        Issue[] issues = issueService.findByUsernameAndRepo(username, repoName);
        ClockifyTask[] clockifyTasks = clockifyService.getTaskFromWorkspace(repoName, username);
        Map<Issue, ClockifyTask[]> map = Stream.of(issues).collect(
                Collectors.toMap(issue -> issue, issue -> Stream.of(clockifyTasks).filter(clockifyTask -> clockifyTask.getDescription().contains(issue.title)).toArray(ClockifyTask[]::new))
        );
        return map.entrySet().stream().map(entry -> {
            TimeTask timeTask = new TimeTask();
            Issue issue = entry.getKey();
            ClockifyTask[] clockifyTask = entry.getValue();
            timeTask.setDescription(issue.body);
            timeTask.setTitle(issue.title);
            double duration = 0;
            for (ClockifyTask task : clockifyTask) {
                LocalDateTime start = LocalDateTime.parse(task.getTimeInterval().getStart(), DateTimeFormatter.ISO_DATE_TIME);
                LocalDateTime end = LocalDateTime.parse(task.getTimeInterval().getEnd(), DateTimeFormatter.ISO_DATE_TIME);
                Duration difference = Duration.between(start, end);
                duration += (difference.toSeconds() + difference.toMinutes() * 60 + difference.toHours() * 3600 + difference.toDays() * 86400)/60.;
            }
            timeTask.setDuration(duration);
            timeTask.setUsernames(issue.assignees.stream().map(
                    owner -> {
                        User user = userService.findUserByUsername(owner.getLogin());
                        return user.getName() + " " + user.getSurname();
                    }
            ).toList());
            return timeTask;
        }).toList();
    }
}
