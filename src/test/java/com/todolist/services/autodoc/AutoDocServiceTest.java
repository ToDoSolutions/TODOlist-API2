package com.todolist.services.autodoc;

import com.todolist.dtos.autodoc.Request;
import com.todolist.dtos.autodoc.clockify.ClockifyTask;
import com.todolist.entity.Group;
import com.todolist.entity.User;
import com.todolist.services.ClockifyService;
import com.todolist.services.TaskService;
import com.todolist.services.github.IssueService;
import com.todolist.services.group.GroupService;
import com.todolist.services.group.GroupTaskService;
import com.todolist.services.user.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
class AutoDocServiceTest {

    @InjectMocks
    private AutoDocService autoDocService;

    @Mock
    private ClockifyService clockifyService;

    @Mock
    private IssueService issueService;

    @Mock
    private UserService userService;

    @Mock
    private TaskService taskService;

    @Mock
    private GroupService groupService;

    @Mock
    private GroupTaskService groupTaskService;

    @Test
    void testAutoDoc() throws IOException {
        Request request = new Request();
        request.setRepoName("repoName");
        request.setUsername("username");

        Group group = new Group();
        when(groupService.findGroupByName(request.getRepoName())).thenReturn(group);

        ClockifyTask clockifyTask = new ClockifyTask();
        when(clockifyService.getTaskFromWorkspace(eq(request.getRepoName()), eq(request.getUsername())))
                .thenReturn(List.of(clockifyTask));

        autoDocService.autoDoc(request);

        verify(groupService, times(1)).findGroupByName(request.getRepoName());
        verify(clockifyService, times(1)).getTaskFromWorkspace(eq(request.getRepoName()), eq(request.getUsername()));
        verify(autoDocService, times(1)).groupIssuesWithTime(request);
    }

    @Test
    void testGroupIssuesWithTime() throws IOException {
        Request request = new Request();
        request.setRepoName("repoName");
        request.setUsername("username");
        request.setIndividual(false);

        Group group = new Group();
        User user = new User();

        when(groupService.findGroupByName(request.getRepoName())).thenReturn(group);
        when(clockifyService.getTaskFromWorkspace(eq(request.getRepoName()), eq(request.getUsername())))
                .thenReturn(List.of(new ClockifyTask()));
        when(userService.findUserByIdClockify(any())).thenReturn(user);

        autoDocService.groupIssuesWithTime(request);

        verify(groupService, times(1)).findGroupByName(request.getRepoName());
        verify(clockifyService, times(1)).getTaskFromWorkspace(eq(request.getRepoName()), eq(request.getUsername()));
        verify(userService, times(1)).findUserByIdClockify(any());
        verify(taskService, times(1)).saveTask(any(), any(), any(), any());
    }

    // Add more test methods if necessary
}

