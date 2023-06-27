package com.todolist.services.github;

import com.todolist.entity.Group;
import com.todolist.exceptions.NotFoundException;
import com.todolist.services.group.GroupUserService;
import com.todolist.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RepoService {

    // Services ---------------------------------------------------------------
    private final GroupUserService groupUserService;
    private final UserService userService;

    // Constructors -----------------------------------------------------------
    @Autowired
    public RepoService(GroupUserService groupUserService, UserService userService) {
        this.groupUserService = groupUserService;
        this.userService = userService;
    }

    // Finders ----------------------------------------------------------------
    @Transactional(readOnly = true)
    public Group findGroupByRepo(String username, String repoName) {
        return groupUserService.findGroupsWithUser(userService.findUserByUsername(username)).stream()
                .filter(group -> group.getName().equals(repoName))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("The group with name " + repoName + " does not exist."));
    }
}
