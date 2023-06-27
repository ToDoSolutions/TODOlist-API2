package com.todolist.services.group;

import com.todolist.dtos.ShowGroup;
import com.todolist.dtos.ShowUser;
import com.todolist.entity.Group;
import com.todolist.exceptions.NotFoundException;
import com.todolist.repositories.GroupRepository;
import com.todolist.services.user.UserService;
import com.todolist.services.user.UserTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {

    // Services ---------------------------------------------------------------
    private final UserService userService;
    private final GroupUserService groupUserService;
    private final UserTaskService userTaskService;

    // Repositories -----------------------------------------------------------
    private final GroupRepository groupRepository;

    // Constructors -----------------------------------------------------------
    @Autowired
    public GroupService(GroupRepository groupRepository, UserService userService, GroupUserService groupUserService, UserTaskService userTaskService) {
        this.groupRepository = groupRepository;
        this.userService = userService;
        this.groupUserService = groupUserService;
        this.userTaskService = userTaskService;
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
}
