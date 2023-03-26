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
        Optional<Role> optionalRole = task.getRole(roleStatus);
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
