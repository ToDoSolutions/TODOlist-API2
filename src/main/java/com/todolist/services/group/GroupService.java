package com.todolist.services.group;

import com.fadda.common.tuples.pair.Pair;
import com.todolist.entity.Group;
import com.todolist.exceptions.NotFoundException;
import com.todolist.repositories.GroupRepository;
import com.todolist.services.BaseService;
import com.todolist.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService extends BaseService<Group> {
    // Services ---------------------------------------------------------------
    private final RoleService roleService;
    private final GroupTaskService groupTaskService;

    // Repositories -----------------------------------------------------------
    private final GroupRepository groupRepository;

    @PostConstruct
    public void init() throws IOException {
        super.init();
    }


    @Override
    @Transactional
    public void saveEntity(Group entity) {
        saveGroup(entity);
    }

    @Override
    protected Class<Group> getEntityClass() {
        return Group.class;
    }

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
        return groupRepository.countTaskInGroup(group.getId());
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
        groupTaskService.getTasksFromGroup(group).stream()
                .filter(task -> task.getUser().getUsername().equalsIgnoreCase(individual))
                .flatMap(task -> roleService.findRoleByTaskId(task.getId()).stream())
                .forEach(roleService::resetRole);
    }

    public void resetRolesForGroup(Group group) {
        groupTaskService.getTasksFromGroup(group).stream()
                .flatMap(task -> roleService.findRoleByTaskId(task.getId()).stream())
                .forEach(roleService::resetRole);
    }
}
