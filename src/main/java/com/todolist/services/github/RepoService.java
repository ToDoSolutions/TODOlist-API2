package com.todolist.services.github;

import com.todolist.entity.Group;
import com.todolist.exceptions.NotFoundException;
import com.todolist.services.group.GroupUserService;
import com.todolist.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RepoService {

    // Services ---------------------------------------------------------------
    private final GroupUserService groupUserService;
    private final UserService userService;

    // Finders ----------------------------------------------------------------
    @Transactional(readOnly = true)
    public Group findGroupByRepo(String username, String repoName) {
        return groupUserService.findGroupsWithUser(userService.findUserByUsername(username)).stream()
                .filter(group -> group.getName().equals(repoName))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("The group with name " + repoName + " does not exist."));
    }
}
