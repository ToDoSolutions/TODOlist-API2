package com.todolist.services.autodoc;

import com.todolist.dtos.autodoc.Area;
import com.todolist.dtos.autodoc.Request;
import com.todolist.entity.Group;
import com.todolist.entity.User;
import com.todolist.repositories.PlanningRepository;
import com.todolist.services.RoleService;
import com.todolist.services.github.IssueService;
import com.todolist.services.group.GroupService;
import com.todolist.services.group.GroupTaskService;
import com.todolist.services.group.GroupUserService;
import com.todolist.services.user.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
class PlanningServiceTest {

    @InjectMocks
    private PlanningService planningService;

    @Mock
    private IssueService issueService;

    @Mock
    private UserService userService;

    @Mock
    private GroupService groupService;

    @Mock
    private RoleService roleService;

    @Mock
    private AutoDocService autoDocService;

    @Mock
    private GroupUserService groupUserService;

    @Mock
    private PlanningRepository planningRepository;

    @Mock
    private GroupTaskService groupTaskService;

    @Test
    void testGetPlanningData() throws IOException {
        Request request = new Request();
        request.setRepoName("repoName");
        request.setIndividual("individual");
        request.setArea(Area.GROUP);

        Group group = new Group();
        when(groupService.findGroupByName(request.getRepoName())).thenReturn(group);

        List<User> users = List.of(new User());
        when(groupUserService.getUsersFromGroup(group)).thenReturn(users);

        Map<String, Double> roleCostMap = Map.of("Role1", 100.0, "Role2", 200.0);
        when(roleService.getTotalCostByRole(any(), any(), any())).thenReturn(roleCostMap);

        PlanningService.PlanningData result = planningService.getPlanningData(request);

        assertNotNull(result);
        assertEquals(group, result.group());
        assertEquals(users, result.users());
        assertEquals(roleCostMap, result.costByRole());

        verify(groupService, times(1)).findGroupByName(request.getRepoName());
        verify(groupUserService, times(1)).getUsersFromGroup(group);
        verify(roleService, times(users.size())).getTotalCostByRole(any(), eq(group), any());
    }

    // Add more test methods if necessary
}

