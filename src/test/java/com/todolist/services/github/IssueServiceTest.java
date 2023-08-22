package com.todolist.services.github;

import com.todolist.dtos.autodoc.Request;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.services.TaskService;
import com.todolist.services.user.UserService;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class IssueServiceTest {

    @InjectMocks
    private IssueService issueService;

    @Mock
    private UserService userService;

    @Mock
    private TaskService taskService;

    @Test
    void testFindByUsernameAndRepo() throws IOException {
        Request request = new Request();
        request.setUsername("username");
        request.setPath("repo/path");
        request.setIndividual("individual");
        request.setIsIndividual(true);

        User user = new User();
        user.setToken("token");
        when(userService.findUserByUsername(request.getUsername())).thenReturn(user);

        GitHub github = mock(GitHub.class);
        GHRepository repository = mock(GHRepository.class);
        when(github.getRepository(request.getPath())).thenReturn(repository);
        List<GHIssue> issues = new ArrayList<>();
        GHIssue issue1 = mock(GHIssue.class);
        GHIssue issue2 = mock(GHIssue.class);
        when(issue1.isPullRequest()).thenReturn(false);
        when(issue2.isPullRequest()).thenReturn(false);
        issues.add(issue1);
        issues.add(issue2);
        when(repository.getIssues(GHIssueState.ALL)).thenReturn(issues);

        GHIssue.Assignee assignee = mock(GHIssue.Assignee.class);
        when(assignee.getLogin()).thenReturn(request.getIndividual());
        when(issue1.getAssignees()).thenReturn(List.of(assignee));
        when(issue2.getLabels()).thenReturn(List.of(new GHLabel().setName(request.getIndividual())));

        List<GHIssue> result = issueService.findByUsernameAndRepo(request);

        assertEquals(2, result.size());
        verify(userService, times(1)).findUserByUsername(request.getUsername());
        verify(github, times(1)).getRepository(request.getPath());
    }

    @Test
    void testGetTaskPerIssue() throws IOException {
        Request request = new Request();

        List<GHIssue> issues = new ArrayList<>();
        GHIssue issue1 = new GHIssue();
        GHIssue issue2 = new GHIssue();
        issues.add(issue1);
        issues.add(issue2);

        List<Task> tasks = new ArrayList<>();
        Task task1 = new Task();
        Task task2 = new Task();
        tasks.add(task1);
        tasks.add(task2);

        when(taskService.findAllTasks()).thenReturn(tasks);

        Map<String, List<Task>> result = issueService.getTaskPerIssue(request);

        assertEquals(2, result.size());
        verify(taskService, times(1)).findAllTasks();
    }

    // Add more test methods for other service methods
}

