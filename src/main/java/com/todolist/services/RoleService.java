package com.todolist.services;

import com.todolist.dtos.autodoc.clockify.TimeInterval;
import com.todolist.entity.Role;
import com.todolist.entity.Task;
import com.todolist.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    // Repositories -----------------------------------------------------------
    private final RoleRepository roleRepository;

    // Finders ----------------------------------------------------------------
    public List<Role> findRoleByTaskId(Integer taskId) {
        return roleRepository.findAllByTaskId(taskId);
    }

    public Duration getDuration(Task task) {
        return roleRepository.findAllDurationByTaskId(task.getId()).stream()
                .reduce(Duration.ZERO, Duration::plus);
    }

    public List<Role> getStatus(Task task) {
        return roleRepository.findAllStatusByTaskId(task.getId());
    }

    public Double getCost(Task task) {
        return findRoleByTaskId(task.getId()).stream()
                .mapToDouble(Role::getSalary)
                .sum();
    }

    // Save and delete --------------------------------------------------------
    @Transactional
    public void saveRole(String tagName, TimeInterval timeInterval, Task task) {
        if (tagName == null)
            return;
        Role role = roleRepository.findRoleByTaskIdAndTagName(task.getId(), tagName)
                .orElseGet(() -> new Role(tagName, task));
        LocalDateTime start = timeInterval.getStartAsLocalDateTime();
        LocalDateTime end = timeInterval.getEndAsLocalDateTime();
        role.addDuration(start, end);
        roleRepository.save(role);
    }

    @Transactional
    public void deleteAllRoles(Task task) {
        roleRepository.deleteAll(findRoleByTaskId(task.getId()));
    }

    public void resetRole(Role role) {
        role.setDuration(Duration.ZERO);
        roleRepository.save(role);
    }
}
