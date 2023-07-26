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

    @Transactional
    public void saveRole(String name, TimeInterval timeInterval, Task task) {
        if (name == null)
            return;
        Role role = roleRepository.findRoleByTaskIdAndTagName(task.getId(), name)
                .orElseGet(() -> createNewRole(name, task));
        LocalDateTime start = timeInterval.getStartAsLocalDateTime();
        LocalDateTime end = timeInterval.getEndAsLocalDateTime();
        role.addDuration(start, end);
        roleRepository.save(role);
    }

    @Transactional
    public void deleteAllRoles(Task task) {
        roleRepository.deleteAll(findRoleByTaskId(task.getId()));
    }

    private Role createNewRole(String tagName, Task task) {
        Role newRole = new Role();
        newRole.setDuration(Duration.ZERO);
        String[] data = tagName.split("-");
        String name = data[0];
        Double salary = Double.parseDouble(data[1]);
        newRole.setTagName(tagName);
        newRole.setName(name);
        newRole.setTask(task);
        newRole.setSalary(salary);
        return newRole;
    }

    public void resetRole(Role role) {
        role.setDuration(Duration.ZERO);
        roleRepository.save(role);
    }
}
