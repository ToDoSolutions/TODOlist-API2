package com.todolist.services.group;

import com.todolist.entity.Group;
import com.todolist.repositories.GroupRepository;
import com.todolist.services.RoleService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
class GroupServiceTest {

    @InjectMocks
    private GroupService groupService;

    @Mock
    private RoleService roleService;

    @Mock
    private GroupTaskService groupTaskService;

    @Mock
    private GroupRepository groupRepository;

    @Test
    void testFindAllGroups() {
        List<Group> groups = new ArrayList<>();
        groups.add(new Group());
        groups.add(new Group());

        when(groupRepository.findAll(any(Sort.class))).thenReturn(groups);

        List<Group> result = groupService.findAllGroups(Sort.unsorted());

        assertEquals(2, result.size());
        verify(groupRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void testFindGroupById() {
        Group group = new Group();
        group.setId(123);

        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

        Group result = groupService.findGroupById(group.getId());

        assertNotNull(result);
        assertEquals(group.getId(), result.getId());
        verify(groupRepository, times(1)).findById(group.getId());
    }

    @Test
    void testFindGroupByName() {
        Group group = new Group();
        group.setName("Test Group");

        when(groupRepository.findByName(group.getName())).thenReturn(Optional.of(group));

        Group result = groupService.findGroupByName(group.getName());

        assertNotNull(result);
        assertEquals(group.getName(), result.getName());
        verify(groupRepository, times(1)).findByName(group.getName());
    }

    // Add more test methods for other service methods
}

