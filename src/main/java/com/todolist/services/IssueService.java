package com.todolist.services;

import com.todolist.component.FetchApiData;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.exceptions.NotFoundException;
import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IssueService {

    // Services ---------------------------------------------------------------
    private final UserService userService;
    private final TaskService taskService;

    // Constructors -----------------------------------------------------------
    @Autowired
    public IssueService(UserService userService, FetchApiData fetchApiData, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }

    // Methods ----------------------------------------------------------------
    public List<GHIssue> findByUsernameAndRepo(String username, String repoName) { // Get all issues from a repo
        User user = userService.findUserByUsername(username);
        List<GHIssue> issues;
        try {
            GitHub github;
            if (user.getToken() == null)
                github = new GitHubBuilder().build();
            else
                github = new GitHubBuilder().withOAuthToken(user.getToken()).build();
            GHRepository repository = github.getRepository(user.getUsername() + "/" + repoName);
            issues = repository.getIssues(GHIssueState.ALL).stream()
                    .filter(issue -> !issue.isPullRequest()).toList();
        } catch (IOException e) {
            throw new NotFoundException("Issues not found for user " + username + " and repo " + repoName + ".");
        }
        return issues;
    }

    public Map<String, List<Task>> getTaskPerIssue(String repoName, String username) {
        List<GHIssue> issues = findByUsernameAndRepo(username, repoName);
        List<Task> tasks = taskService.findAllTasks();
        Map<String, List<Task>> result = new HashMap<>();
        for (GHIssue issue : issues) {
            List<Task> tasksIssue = tasks.stream().filter(task -> task.getTitle().contains(issue.getTitle())).toList();
            result.put(issue.getTitle(), tasksIssue);
        }
        return result;
    }

    public Map<String, List<Task>> getTaskPerIssueFilter(String repoName, String username, String title, String individual) {
        return getTaskPerIssue(repoName, username).entrySet().stream()
                .filter(entry -> entry.getValue().stream().anyMatch(task -> task.getUser().getUsername().equals(individual) && task.getTitle().contains(title)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                .entrySet().stream().sorted(Comparator.comparing(entry -> entry.getValue().get(0)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    public Map<String, List<Task>> getTaskPerIssueFilter(String repoName, String username, String title) {
        return getTaskPerIssue(repoName, username).entrySet().stream()
                .filter(entry -> entry.getValue().stream().anyMatch(task -> task.getTitle().contains(title)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                .entrySet().stream().sorted(Comparator.comparing(entry -> entry.getValue().get(0)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }
}
