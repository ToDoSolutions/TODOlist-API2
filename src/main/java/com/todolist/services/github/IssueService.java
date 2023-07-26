package com.todolist.services.github;

import com.todolist.dtos.autodoc.Request;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.exceptions.RequestTimeoutException;
import com.todolist.services.TaskService;
import com.todolist.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IssueService {

    // Services ---------------------------------------------------------------
    private final UserService userService;
    private final TaskService taskService;

    // Finders ----------------------------------------------------------------
    public List<GHIssue> findByUsernameAndRepo(Request request) throws IOException {
        User user = userService.findUserByUsername(request.getUsername());
        GitHub github = createGitHubInstance(user);
        GHRepository repository = github.getRepository(request.getPath());
        return repository.getIssues(GHIssueState.ALL)
                .stream()
                .filter(issue -> !issue.isPullRequest() && ((issue.getAssignees().stream().anyMatch(assignee -> assignee.getLogin().equals(request.getIndividual())) || issue.getLabels().stream().anyMatch(label -> label.getName().equals(request.getIndividual())) || !request.isIndividual())))
                .toList();
    }

    public Map<String, List<Task>> getTaskPerIssue(Request request) throws IOException {
        List<GHIssue> issues = findByUsernameAndRepo(request);
        List<Task> tasks = taskService.findAllTasks();
        return issues.stream()
                .collect(Collectors.toMap(
                        GHIssue::getTitle,
                        issue -> filterTasksByIssue(tasks, issue)
                ));
    }

    private List<Task> filterTasksByIssue(List<Task> tasks, GHIssue issue) {
        String issueTitle = issue.getTitle();
        return tasks.stream()
                .filter(task -> task.getTitle().contains(issueTitle))
                .toList();
    }

    public Map<String, List<Task>> getTaskPerIssueFilter(Request request) throws IOException {
        return getTaskPerIssue(request)
                .entrySet()
                .stream()
                .filter(entry -> filterTasksByRequest(entry.getValue(), request))
                .sorted(Comparator.comparing(entry -> entry.getValue().get(0)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    private boolean filterTasksByRequest(List<Task> tasks, Request request) {
        return tasks.stream()
                .anyMatch(task -> isMatchingTask(task, request));
    }

    // Creators ---------------------------------------------------------------
    private GitHub createGitHubInstance(User user) {
        Callable<GitHub> callable = () -> {
            if (user.getToken() == null) {
                return new GitHubBuilder().build();
            } else {
                return new GitHubBuilder().withOAuthToken(user.getToken()).build();
            }
        };

        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            return executor.submit(callable).get(1, TimeUnit.MINUTES);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RequestTimeoutException("Petition to GitHub exceeded!!");
        } finally {
            executor.shutdown();
        }
    }


    // Others -----------------------------------------------------------------
    private boolean isMatchingTask(Task task, Request request) {
        return switch (request.getArea()) {
            case ALL -> true;
            case INDIVIDUAL -> task.getStudent() != 0;
            case GROUP -> task.getStudent() == 0;
        };
    }
}
