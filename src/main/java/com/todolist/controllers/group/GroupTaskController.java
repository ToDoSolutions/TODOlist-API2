package com.todolist.controllers.group;

import com.todolist.dtos.ShowGroup;
import com.todolist.entity.Group;
import com.todolist.entity.Task;
import com.todolist.services.TaskService;
import com.todolist.services.group.GroupService;
import com.todolist.services.group.GroupTaskService;
import com.todolist.services.group.GroupUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GroupTaskController {

    // Services ---------------------------------------------------------------
    private final GroupService groupService;
    private final GroupTaskService groupTaskService;
    private final GroupUserService groupUserService;
    private final TaskService taskService;

    /* ------------ */
    // CRUD Methods //
    /* ------------ */


    // Getters -----------------------------------------------------------------
    @GetMapping("/groups/task/{idTask}")
    public ResponseEntity<List<ShowGroup>> getGroupsWithTask(@PathVariable("idTask") @Min(value = 0, message = "The idTask must be positive.") Integer idTask) {
        Task task = taskService.findTaskById(idTask);
        List<Group> groups = groupTaskService.findGroupsWithTask(task);
        List<ShowGroup> showGroups = groups.stream().map(group -> new ShowGroup(group, groupUserService.getShowUsersFromGroup(group))).toList();
        return ResponseEntity.ok(showGroups);
    }

    // Adders ------------------------------------------------------------------
    @PutMapping("/group/{idGroup}/task/{idTask}")
    public ResponseEntity<ShowGroup> addTaskToGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Integer idGroup,
                                                    @PathVariable("idTask") @Min(value = 0, message = "The idTask must be positive.") Integer idTask) {
        Group group = groupService.findGroupById(idGroup);
        Task task = taskService.findTaskById(idTask);
        if (groupTaskService.hasGroupTheTask(group, task))
            groupTaskService.addTaskToGroup(group, task);
        ShowGroup showGroup = new ShowGroup(group, groupUserService.getShowUsersFromGroup(group));
        return ResponseEntity.ok(showGroup);
    }

    // Deleters ----------------------------------------------------------------
    @DeleteMapping("/group/{idGroup}/tasks")
    public ResponseEntity<ShowGroup> deleteAllTasksFromGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Integer idGroup) {
        Group group = groupService.findGroupById(idGroup);
        groupTaskService.removeAllTasksFromGroup(group);
        ShowGroup showGroup = new ShowGroup(group, groupUserService.getShowUsersFromGroup(group));
        return ResponseEntity.ok(showGroup);
    }

    @DeleteMapping("group/{idGroup}/task/{idTask}")
    public ResponseEntity<ShowGroup> deleteTaskFromGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Integer idGroup,
                                                         @PathVariable("idTask") @Min(value = 0, message = "The idTask must be positive.") Integer idTask) {
        Group group = groupService.findGroupById(idGroup);
        Task task = taskService.findTaskById(idTask);
        if (groupTaskService.hasGroupTheTask(group, task))
            groupTaskService.removeTaskFromGroup(group, task);
        ShowGroup showGroup = new ShowGroup(group, groupUserService.getShowUsersFromGroup(group));
        return ResponseEntity.ok(showGroup);
    }
}
