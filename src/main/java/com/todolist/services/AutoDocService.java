package com.todolist.services;

import com.todolist.entity.User;
import com.todolist.entity.autodoc.Role;
import com.todolist.entity.autodoc.TimeTask;
import com.todolist.entity.autodoc.clockify.ClockifyTask;
import com.todolist.entity.autodoc.github.Issue;
import com.todolist.services.github.IssueService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
            double cost = 0;
            Set<Role> allRoles = new HashSet<>();
            for (ClockifyTask task : clockifyTask) {
                List<Role> roles = task.getTagIds().stream().map(tagId -> clockifyService.getRoleFromClockify(repoName, tagId)).distinct().toList();
                LocalDateTime start = LocalDateTime.parse(task.getTimeInterval().getStart(), DateTimeFormatter.ISO_DATE_TIME);
                LocalDateTime end = LocalDateTime.parse(task.getTimeInterval().getEnd(), DateTimeFormatter.ISO_DATE_TIME);
                Duration difference = Duration.between(start, end);
                duration += (difference.toSeconds()/3600.) + (difference.toMinutes()/60.) + difference.toHours() *  + difference.toDays() * 24;
                double finalDuration = duration;
                cost += roles.stream().mapToDouble(role -> role.getFinalSalary(finalDuration)).sum();
                allRoles.addAll(roles);
            }
            timeTask.setDuration(duration);
            timeTask.setCost(cost);
            timeTask.setRoles(allRoles);
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
