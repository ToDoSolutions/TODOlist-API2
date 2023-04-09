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
import java.util.Optional;

@Service
@AllArgsConstructor
public class RoleService {

    // Repository ---------------------------------------------------------------
    private final RoleRepository roleRepository;


    // Finders -----------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<Role>findRoleByTaskId(Integer taskId) {
        return roleRepository.findAllByTaskId(taskId);
    }

    @Transactional(readOnly = true)
    public Duration getDuration(Task task) {
        return findRoleByTaskId(task.getId()).stream().map(Role::getDuration).reduce(Duration.ZERO, Duration::plus);
    }

    @Transactional(readOnly = true)
    public Optional<Role> getRole(Task task, RoleStatus status) {
        return findRoleByTaskId(task.getId()).stream().filter(role -> role.getStatus().equals(status)).findFirst();
    }

    @Transactional(readOnly = true)
    public List<RoleStatus> getStatus(Task task) {
        return findRoleByTaskId(task.getId()).stream().map(Role::getStatus).toList();
    }

    @Transactional(readOnly = true)
    public Double getCost(Task task) {
        return findRoleByTaskId(task.getId()).stream().mapToDouble(Role::getSalary).sum();
    }



    // Save and delete --------------------------------------------------------

    @Transactional
    public void saveRole(RoleStatus roleStatus, TimeInterval timeInterval, Task task) {
        Optional<Role> optionalRole = getRole(task, roleStatus);
        Role role = optionalRole.orElseGet(() -> {
            Role newRole = new Role();
            newRole.setDuration(Duration.ZERO);
            newRole.setStatus(roleStatus);
            newRole.setTask(task);
            return newRole;
        });
        LocalDateTime start = timeInterval.getStartAsLocalDateTime();
        LocalDateTime end = timeInterval.getEndAsLocalDateTime();
        role.addDuration(start, end);
        roleRepository.save(role);
    }

    @Transactional
    public void deleteAllRoles(Task task) {
        roleRepository.deleteAll(findRoleByTaskId(task.getId()));
    }
}
