package com.todolist.services;

import com.todolist.dtos.autodoc.RoleStatus;
import com.todolist.entity.Role;
import com.todolist.entity.Task;
import com.todolist.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    // Repository ---------------------------------------------------------------
    private final RoleRepository roleRepository;

    // Constructors -------------------------------------------------------------
    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // Finders -----------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Role findRoleById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Role> findRoleByStatus(RoleStatus name) {
        return roleRepository.findAllByStatus(name);
    }

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
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(Role role) {
        roleRepository.delete(role);
    }

    public Role saveRole(RoleStatus roleStatus, LocalDateTime start, LocalDateTime end, Task task) {
        Optional<Role> optionalRole = getRole(task, roleStatus);
        Role role;
        if (optionalRole.isPresent()) {
            role = optionalRole.get();
            role.addDuration(start, end);
        } else {
            role = new Role();
            role.setStatus(roleStatus);
            role.setDuration(Duration.between(start, end));
            role.setTask(task);
        }
        return roleRepository.save(role);
    }
}
