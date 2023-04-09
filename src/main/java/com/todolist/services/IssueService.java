package com.todolist.services;

import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.exceptions.RequestTimeoutException;
import lombok.AllArgsConstructor;
import org.kohsuke.github.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class IssueService {

    // Services ---------------------------------------------------------------
    private final UserService userService;
    private final TaskService taskService;

    // Methods ----------------------------------------------------------------
    public List<GHIssue> findByUsernameAndRepo(String username, String repoName) throws TimeoutException { // Get all issues from a repo
        User user = userService.findUserByUsername(username);
        List<GHIssue> issues;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<GHIssue>> future = executor.submit(() -> {
            // Aquí va el código que deseas ejecutar con límite de tiempo
            GitHub github;
            if (user.getToken() == null)
                github = new GitHubBuilder().build();
            else
                github = new GitHubBuilder().withOAuthToken(user.getToken()).build();
            GHRepository repository = github.getRepository(user.getUsername() + "/" + repoName);
            return repository.getIssues(GHIssueState.ALL).stream()
                    .filter(issue -> !issue.isPullRequest()).toList();
        });
        try {
            issues = future.get(1, TimeUnit.MINUTES); // Establece el límite de tiempo en 1 minuto
            // Usa la lista de issues aquí...
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            // Si se produce una excepción de tiempo de espera, maneja la excepción aquí...
            throw new RequestTimeoutException("Petition to github exceeded!!");
        }
        executor.shutdown();
        return issues;
    }

    public Map<String, List<Task>> getTaskPerIssue(String repoName, String username) throws TimeoutException {
        List<GHIssue> issues = findByUsernameAndRepo(username, repoName);
        List<Task> tasks = taskService.findAllTasks();
        Map<String, List<Task>> result = new HashMap<>();
        for (GHIssue issue : issues) {
            List<Task> tasksIssue = tasks.stream().filter(task -> task.getTitle().contains(issue.getTitle())).toList();
            result.put(issue.getTitle(), tasksIssue);
        }
        return result;
    }

    public Map<String, List<Task>> getTaskPerIssueFilter(String repoName, String username, String title, String individual) throws TimeoutException {
        return getTaskPerIssue(repoName, username).entrySet().stream()
                .filter(entry -> entry.getValue().stream().anyMatch(task -> task.getUser().getUsername().equals(individual) && task.getTitle().contains(title)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                .entrySet().stream().sorted(Comparator.comparing(entry -> entry.getValue().get(0)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    public Map<String, List<Task>> getTaskPerIssueFilter(String repoName, String username, String title) throws TimeoutException {
        return getTaskPerIssue(repoName, username).entrySet().stream()
                .filter(entry -> entry.getValue().stream().anyMatch(task -> task.getTitle().contains(title)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                .entrySet().stream().sorted(Comparator.comparing(entry -> entry.getValue().get(0)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }
}
