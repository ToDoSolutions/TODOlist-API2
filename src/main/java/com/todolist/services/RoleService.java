package com.todolist.services;

import com.todolist.dtos.autodoc.RoleStatus;
import com.todolist.dtos.autodoc.clockify.TimeInterval;
import com.todolist.entity.Role;
import com.todolist.entity.Task;
import com.todolist.repositories.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public List<Role> findRoleByTaskId(Integer taskId) {
        return roleRepository.findAllByTaskId(taskId);
    }

    public Duration getDuration(Task task) {
        return roleRepository.findAllDurationByTaskId(task.getId()).stream()
                .reduce(Duration.ZERO, Duration::plus);
    }

    public List<RoleStatus> getStatus(Task task) {
        return roleRepository.findAllStatusByTaskId(task.getId());
    }

    public Double getCost(Task task) {
        return findRoleByTaskId(task.getId()).stream()
                .mapToDouble(Role::getSalary)
                .sum();
    }

    @Transactional
    public void saveRole(RoleStatus roleStatus, TimeInterval timeInterval, Task task) {
        Role role = roleRepository.findRoleByTaskIdAndStatus(task.getId(), roleStatus)
                .orElseGet(() -> createNewRole(roleStatus, task));
        LocalDateTime start = timeInterval.getStartAsLocalDateTime();
        LocalDateTime end = timeInterval.getEndAsLocalDateTime();
        role.addDuration(start, end);
        roleRepository.save(role);
    }

    @Transactional
    public void deleteAllRoles(Task task) {
        roleRepository.deleteAll(findRoleByTaskId(task.getId()));
    }

    private Role createNewRole(RoleStatus roleStatus, Task task) {
        Role newRole = new Role();
        newRole.setDuration(Duration.ZERO);
        newRole.setStatus(roleStatus);
        newRole.setTask(task);
        return newRole;
    }
}
