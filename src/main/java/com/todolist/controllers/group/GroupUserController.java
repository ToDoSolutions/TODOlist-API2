package com.todolist.controllers.group;

import com.todolist.dtos.ShowGroup;
import com.todolist.entity.Group;
import com.todolist.entity.User;
import com.todolist.services.group.GroupService;
import com.todolist.services.group.GroupUserService;
import com.todolist.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GroupUserController {

    // Services ---------------------------------------------------------------
    private final GroupService groupService;
    private final UserService userService;
    private final GroupUserService groupUserService;

    /* ------------ */
    // CRUD Methods //
    /* ------------ */

    // Getters -----------------------------------------------------------------
    @GetMapping("/groups/user/{idUser}")
    public ResponseEntity<List<ShowGroup>> getGroupsWithUser(@PathVariable("idUser") @Min(value = 0, message = "The idUser must be positive.") Integer idUser) {
        User user = userService.findUserById(idUser);
        List<Group> groups = groupUserService.findGroupsWithUser(user);
        List<ShowGroup> showGroups = groups.stream().map(group -> new ShowGroup(group, groupUserService.getShowUsersFromGroup(group))).toList();
        return ResponseEntity.ok(showGroups);
    }

    // Adders ------------------------------------------------------------------
    @PutMapping("/group/{idGroup}/user/{idUser}")
    public ResponseEntity<ShowGroup> addUserToGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Integer idGroup,
                                                    @PathVariable("idUser") @Min(value = 0, message = "The idUser must be positive.") Integer idUser) {
        Group group = groupService.findGroupById(idGroup);
        User user = userService.findUserById(idUser);
        if (!groupUserService.hasUserInGroup(group, user))
            groupUserService.addUserToGroup(group, user);
        ShowGroup showGroup = new ShowGroup(group, groupUserService.getShowUsersFromGroup(group));
        return ResponseEntity.ok(showGroup);
    }

    // Deleters ----------------------------------------------------------------
    @DeleteMapping("/group/{idGroup}/users")
    public ResponseEntity<ShowGroup> deleteAllUsersFromGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Integer idGroup) {
        Group group = groupService.findGroupById(idGroup);
        groupUserService.removeAllUsersFromGroup(group);
        ShowGroup showGroup = new ShowGroup(group, groupUserService.getShowUsersFromGroup(group));
        return ResponseEntity.ok(showGroup);
    }


    @DeleteMapping("/group/{idGroup}/user/{idUser}")
    public ResponseEntity<ShowGroup> deleteUserFromGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Integer idGroup,
                                                         @PathVariable("idUser") @Min(value = 0, message = "The idUser must be positive.") Integer idUser) {
        Group group = groupService.findGroupById(idGroup);
        User user = userService.findUserById(idUser);
        if (groupUserService.hasUserInGroup(group, user))
            groupUserService.removeUserFromGroup(group, user);
        ShowGroup showGroup = new ShowGroup(group, groupUserService.getShowUsersFromGroup(group));
        return ResponseEntity.ok(showGroup);
    }
}
