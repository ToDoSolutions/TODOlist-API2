package com.todolist.services.group;

import com.todolist.dtos.ShowUser;
import com.todolist.entity.Group;
import com.todolist.entity.GroupUser;
import com.todolist.entity.User;
import com.todolist.exceptions.BadRequestException;
import com.todolist.exceptions.NotFoundException;
import com.todolist.repositories.GroupUserRepository;
import com.todolist.services.BaseService;
import com.todolist.services.user.UserTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupUserService extends BaseService<GroupUser> {

    // Services ---------------------------------------------------------------
    private final UserTaskService userTaskService;

    // Repositories -----------------------------------------------------------
    private final GroupUserRepository groupUserRepository;

    @PostConstruct
    @Transactional
    public void init() throws IOException {
        super.init();
    }

    @Override
    protected void saveEntity(GroupUser entity) {
        groupUserRepository.save(entity);
    }

    @Override
    protected Class<GroupUser> getEntityClass() {
        return GroupUser.class;
    }

    GroupUser parseEntity(String headers, String line) {
        GroupUser groupUser = new GroupUser();
        String[] arrayHeaders = headers.split(",");
        String[] arrayValues = line.split(",");
        for (int i = 0; i < arrayHeaders.length; i++) {
            String header = arrayHeaders[i].trim();
            String data = arrayValues[i].trim();
            switch (header) {
                case "id" -> groupUser.setId(Integer.parseInt(data));
                case "id_user" -> groupUser.setIdUser(Integer.parseInt(data));
                case "id_group" -> groupUser.setIdGroup(Integer.parseInt(data));
                default -> throw new IllegalStateException("Invalid");
            }
        }

        return groupUser;
    }

    // Finders ----------------------------------------------------------------
    @Transactional
    public List<ShowUser> getShowUsersFromGroup(Group group) {
        return getUsersFromGroup(group).stream().map(user -> new ShowUser(user, userTaskService.getShowTasksFromUser(user))).toList();
    }

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
