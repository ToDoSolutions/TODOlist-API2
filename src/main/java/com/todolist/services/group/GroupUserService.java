package com.todolist.services.group;

import com.todolist.dtos.ShowUser;
import com.todolist.entity.Group;
import com.todolist.entity.GroupUser;
import com.todolist.entity.User;
import com.todolist.exceptions.BadRequestException;
import com.todolist.exceptions.NotFoundException;
import com.todolist.repositories.GroupUserRepository;
import com.todolist.services.user.UserTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupUserService {

    // Services ---------------------------------------------------------------
    private final UserTaskService userTaskService;

    // Repositories -----------------------------------------------------------
    private final GroupUserRepository groupUserRepository;

    @Transactional
    public List<ShowUser> getShowUsersFromGroup(Group group) {
        return getUsersFromGroup(group).stream().map(user -> new ShowUser(user, userTaskService.getShowTasksFromUser(user))).toList();
    }

    // Finders ----------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<Group> findGroupsWithUser(User user) {
        List<Group> groups = groupUserRepository.findAllByIdUser(user.getId());
        if (groups.isEmpty())
            throw new BadRequestException("The user with idUser " + user.getId() + " does not belong to any group.");
        return groups;
    }

    @Transactional(readOnly = true)
    public List<User> getUsersFromGroup(Group group) {
        List<User> users = groupUserRepository.findAllByIdGroup(group.getId());
        if (users.isEmpty())
            throw new BadRequestException("The group with idGroup " + group.getId() + " does not have any user.");
        return users;
    }


    // Save and Delete --------------------------------------------------------
    @Transactional
    public void addUserToGroup(Group group, User user) {
        groupUserRepository.save(new GroupUser(group.getId(), user.getId()));
    }

    @Transactional
    public void removeUserFromGroup(Group group, User user) {
        List<GroupUser> groupUsers = groupUserRepository.findByIdAndIdUser(group.getId(), user.getId());
        groupUserRepository.deleteAll(groupUsers);
    }

    @Transactional
    public void removeAllUsersFromGroup(Group group) {
        List<GroupUser> groupUsers = groupUserRepository.findByIdGroup(group.getId());
        if (groupUsers.isEmpty()) {
            throw new NotFoundException("The group with idGroup " + group.getId() + " does not exist.");
        }
        groupUserRepository.deleteAll(groupUsers);
    }

    // Others -----------------------------------------------------------------
    @Transactional
    public boolean hasUserInGroup(Group group, User user) {
        return getUsersFromGroup(group).stream()
                .map(User::getId)
                .anyMatch(userId -> userId.equals(user.getId()));
    }
}
