package com.todolist.services.group;

import com.todolist.dtos.ShowUser;
import com.todolist.entity.Group;
import com.todolist.entity.User;
import com.todolist.repositories.GroupUserRepository;
import com.todolist.services.user.UserTaskService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class GroupUserServiceTest {

    @InjectMocks
    private GroupUserService groupUserService;

    @Mock
    private UserTaskService userTaskService;

    @Mock
    private GroupUserRepository groupUserRepository;

    @Test
    void testGetShowUsersFromGroup() {
        Group group = new Group();
        group.setId(789);

        User user1 = new User();
        User user2 = new User();
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        when(groupUserRepository.findAllByIdGroup(group.getId())).thenReturn(users);

        when(userTaskService.getShowTasksFromUser(user1)).thenReturn(new ArrayList<>());
        when(userTaskService.getShowTasksFromUser(user2)).thenReturn(new ArrayList<>());

        List<ShowUser> result = groupUserService.getShowUsersFromGroup(group);

        assertEquals(2, result.size());
        verify(groupUserRepository, times(1)).findAllByIdGroup(group.getId());
        verify(userTaskService, times(2)).getShowTasksFromUser(any(User.class));
    }

    @Test
    void testFindGroupsWithUser() {
        User user = new User();
        user.setId(123);

        Group group1 = new Group();
        Group group2 = new Group();
        List<Group> groups = new ArrayList<>();
        groups.add(group1);
        groups.add(group2);

        when(groupUserRepository.findAllByIdUser(user.getId())).thenReturn(groups);

        List<Group> result = groupUserService.findGroupsWithUser(user);

        assertEquals(2, result.size());
        verify(groupUserRepository, times(1)).findAllByIdUser(user.getId());
    }

    // Add more test methods for other service methods
}

