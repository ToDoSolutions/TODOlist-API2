package com.todolist.services.group;

import com.todolist.entity.Group;
import com.todolist.exceptions.NotFoundException;
import com.todolist.repositories.GroupRepository;
import com.todolist.services.RoleService;
import com.todolist.services.user.UserTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {
    // Services ---------------------------------------------------------------
    private final GroupUserService groupUserService;
    private final UserTaskService userTaskService;
    private final RoleService roleService;

    // Repositories -----------------------------------------------------------
    private final GroupRepository groupRepository;

    // Finders ----------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<Group> findAllGroups(Sort sort) {
        return groupRepository.findAll(sort);
    }

    @Transactional(readOnly = true)
    public Group findGroupById(Integer idGroup) {
        return groupRepository.findById(idGroup).orElseThrow(() -> new NotFoundException("The group with idGroup " + idGroup + " does not exist."));
    }

    @Transactional(readOnly = true)
    public Group findGroupByName(String name) {
        return groupRepository.findByName(name).orElseThrow(() -> new NotFoundException("The group with name " + name + " does not exist."));
    }

    @Transactional(readOnly = true)
    public Long getNumTasks(Group group) {
        return (long) groupUserService.getUsersFromGroup(group).stream()
                .flatMap(user -> user.getTasks().stream())
                .collect(Collectors.toSet()).size();
    }

    // Save and delete --------------------------------------------------------
    @Transactional
    public Group saveGroup(Group group) {
        if (group.getCreatedDate() == null) group.setCreatedDate(LocalDate.now());
        return groupRepository.save(group);
    }

    @Transactional
    public void deleteGroup(Group group) {
        groupRepository.delete(group);
    }

    public void resetRolesForUser(String individual, Group group) {
        groupUserService.getUsersFromGroup(group).stream()
                .filter(user -> user.getUsername().equals(individual))
                .flatMap(user -> userTaskService.getTasksFromUser(user).stream().flatMap(task -> roleService.findRoleByTaskId(task.getId()).stream()))
                .forEach(roleService::resetRole);
    }

    public void resetRolesForGroup(Group group) {
        groupUserService.getUsersFromGroup(group).stream()
                .flatMap(user -> userTaskService.getTasksFromUser(user).stream().flatMap(task -> roleService.findRoleByTaskId(task.getId()).stream()))
                .forEach(roleService::resetRole);
    }
}
