package com.todolist.services;

import com.todolist.entity.Role;
import com.todolist.entity.Task;
import com.todolist.repositories.RoleRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class RoleServiceTest {

    @InjectMocks
    private RoleService roleService;

    @Mock
    private RoleRepository roleRepository;

    @Test
    void testFindRoleByTaskId() {
        Integer taskId = 1;
        List<Role> roles = new ArrayList<>();
        when(roleRepository.findAllByTaskId(taskId)).thenReturn(roles);

        List<Role> result = roleService.findRoleByTaskId(taskId);

        assertEquals(roles, result);
    }

    @Test
    void testGetDuration() {
        Task task = new Task();
        task.setId(1);
        List<Duration> durations = new ArrayList<>();
        durations.add(Duration.ofHours(1));
        durations.add(Duration.ofHours(2));
        when(roleRepository.findAllDurationByTaskId(task.getId())).thenReturn(durations);

        Duration result = roleService.getDuration(task);

        assertEquals(Duration.ofHours(3), result);
    }

    @Test
    void testGetStatus() {
        Task task = new Task();
        task.setId(1);
        List<Role> roles = new ArrayList<>();
        when(roleRepository.findAllStatusByTaskId(task.getId())).thenReturn(roles);

        List<Role> result = roleService.getStatus(task);

        assertEquals(roles, result);
    }

    @Test
    void testGetCost() {
        Task task = new Task();
        task.setId(1);
        List<Role> roles = new ArrayList<>();
        roles.add(new Role(100.0));
        roles.add(new Role(200.0));
        when(roleRepository.findAllByTaskId(task.getId())).thenReturn(roles);

        Double result = roleService.getCost(task);

        assertEquals(300.0, result);
    }

    // Add more test methods for other service methods
}

