package com.todolist.services.github;

import com.todolist.component.FetchApiData;
import com.todolist.dtos.autodoc.github.Issue;
import com.todolist.dtos.autodoc.github.Owner;
import com.todolist.dtos.autodoc.github.TaskGitHub;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.services.TaskService;
import com.todolist.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IssueService {

    // Services ---------------------------------------------------------------
    private final RepoService repoService;
    private final UserService userService;
    private final FetchApiData fetchApiData;
    private final TaskService taskService;

    // Urls -------------------------------------------------------------------
    @Value("${github.api.url}")
    private String startUrl;

    // Constructors -----------------------------------------------------------
    @Autowired
    public IssueService(RepoService repoService, UserService userService, FetchApiData fetchApiData, TaskService taskService) {
        this.repoService = repoService;
        this.userService = userService;
        this.fetchApiData = fetchApiData;
        this.taskService = taskService;
    }

    // Methods ----------------------------------------------------------------
    public List<Issue> findByUsernameAndRepo(String username, String repoName) { // Get all issues from a repo
        User user = userService.findUserByUsername(username);
        TaskGitHub task = repoService.findRepoByName(user.getUsername(), repoName);
        return Arrays.stream(fetchApiData.getApiData(startUrl + "/repos/" + user.getUsername() + "/" + task.getName() + "/issues?state=all&per_page=999", Issue[].class))
                .filter(issue -> !(issue.getTitle().contains("ADD") || issue.getTitle().contains("FIX"))).toList();
    }

    public User getUserAssignedToIssue(Owner owner) {
        return userService.findUserByUsername(owner.getLogin());
    }

    public Map<String, List<Task>> getTaskPerIssue(String repoName, String username) {
        List<Issue> issues = findByUsernameAndRepo(username, repoName);
        List<Task> tasks = taskService.findAllTasks();
        Map<String, List<Task>> result = new HashMap<>();
        for (Issue issue: issues) {
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
