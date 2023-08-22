package com.todolist.services.github;

import com.todolist.entity.Group;
import com.todolist.entity.User;
import com.todolist.services.group.GroupUserService;
import com.todolist.services.user.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
class RepoServiceTest {

    @InjectMocks
    private RepoService repoService;

    @Mock
    private GroupUserService groupUserService;

    @Mock
    private UserService userService;

    @Test
    void testFindGroupByRepo() {
        String username = "username";
        String repoName = "repoName";

        User user = new User();
        when(userService.findUserByUsername(username)).thenReturn(user);

        Group group = new Group();
        when(groupUserService.findGroupsWithUser(user)).thenReturn(List.of(group));

        Group result = repoService.findGroupByRepo(username, repoName);

        assertNotNull(result);
        assertEquals(group, result);
        verify(userService, times(1)).findUserByUsername(username);
        verify(groupUserService, times(1)).findGroupsWithUser(user);
    }

    // Add more test methods if necessary
}

