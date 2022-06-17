package com.todolist.resources;

import com.todolist.entity.Group;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.model.ShowGroup;
import com.todolist.model.ShowTask;
import com.todolist.model.ShowUser;
import com.todolist.parsers.GroupParser;
import com.todolist.repository.GroupRepository;
import com.todolist.repository.TaskRepository;
import com.todolist.repository.UserRepository;
import com.todolist.utilities.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/groups")
@Validated
public class GroupResource {

    @Autowired
    @Qualifier("groupRepository")
    private GroupRepository groupRepository;

    @Autowired
    @Qualifier("userRepository")
    private UserRepository userRepository;

    @Autowired
    @Qualifier("taskRepository")
    private TaskRepository taskRepository;

    @Autowired
    @Qualifier("groupParser")
    private GroupParser groupParser;

    @GetMapping
    public List<Map<String, Object>> getAllGroups(@RequestParam(defaultValue = "0") @Min(value = 0, message = "The offset must be positive.") Integer offset,
                                                  @RequestParam(defaultValue = Integer.MAX_VALUE + "") @Min(value = 0, message = "The limit must be positive") Integer limit,
                                                  @RequestParam(defaultValue = "idGroup") String order,
                                                  @RequestParam(defaultValue = ShowGroup.ALL_ATTRIBUTES) String fieldsGroup,
                                                  @RequestParam(defaultValue = ShowUser.ALL_ATTRIBUTES) String fieldsUser,
                                                  @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES) String fieldsTask,
                                                  @RequestParam(required = false) String name,
                                                  @RequestParam(required = false) String description,
                                                  @RequestParam(required = false) @Pattern(regexp = "[<>=]{2}\\d+|[<>=]\\d+", message = "The tasks' number is invalid.") String numTasks,
                                                  @RequestParam(required = false) @Pattern(regexp = "[<>=]{2}\\d{4}-\\d{2}-\\d{2}|[<>=]\\d{4}-\\d{2}-\\d{2}", message = "The createdDate is invalid.") String createdDate) {
        List<ShowGroup> result = new ArrayList<>(),
                groups = groupParser.parseList(groupRepository.findAll(PageRequest.of(offset, limit, Sort.by(order.charAt(0) == '-' ? Sort.Direction.DESC : Sort.Direction.ASC, order.charAt(0) == '+' || order.charAt(0) == '-' ? order.substring(1, order.length() - 1) : order))).getContent());
        for (ShowGroup group : groups) {
            if (group != null &&
                    (name == null || group.getName().equals(name)) &&
                    (description == null || group.getDescription().equals(description)) &&
                    (numTasks == null || Filter.isGEL((long) group.getNumTasks(), numTasks)) &&
                    (createdDate == null || Filter.isGEL(group.getCreatedDate(), createdDate)))
                result.add(group);
        }
        return result.stream().map(group -> group.getFields(fieldsGroup, fieldsUser, fieldsTask)).collect(Collectors.toList());
    }

    @GetMapping("/{idGroup}")
    public Map<String, Object> getGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup,
                                        @RequestParam(defaultValue = ShowGroup.ALL_ATTRIBUTES) String fieldsGroup,
                                        @RequestParam(defaultValue = ShowUser.ALL_ATTRIBUTES) String fieldsUser,
                                        @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES) String fieldsTask) {
        Group group = groupRepository.findById(idGroup).orElse(null);
        if (group == null)
            throw new NullPointerException("The group with idGroup " + idGroup + " does not exist.|uri=/api/v1/groups/" + idGroup);
        return new ShowGroup(group).getFields(fieldsGroup, fieldsUser, fieldsTask);
    }

    @PostMapping
    public Map<String, Object> addGroup(@RequestBody @Valid Group group) {
        if (group.getName() == null)
            throw new IllegalArgumentException("The group with idGroup " + group.getIdGroup() + " must have name.|uri=/api/v1/groups/");
        else if (group.getUsers() != null)
            throw new IllegalArgumentException("The group with idGroup " + group.getIdGroup() + " must not have users.|uri=/api/v1/groups/");
        groupRepository.save(group);
        return new ShowGroup(group).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @PutMapping
    public Map<String, Object> updateGroup(@RequestBody @Valid Group group) {
        Group oldGroup = groupRepository.findById(group.getIdGroup()).orElse(null);
        if (oldGroup == null)
            throw new NullPointerException("The group with idGroup " + group.getIdGroup() + " does not exist.|uri=/api/v1/groups/" + group.getIdGroup());
        if (group.getName() != null)
            oldGroup.setName(group.getName());
        if (group.getDescription() != null)
            oldGroup.setDescription(group.getDescription());
        // Update avatar.
        if (group.getCreatedDate() != null)
            oldGroup.setCreatedDate(group.getCreatedDate());
        groupRepository.save(oldGroup);
        return new ShowGroup(oldGroup).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @DeleteMapping("/{idGroup}")
    public Map<String, Object> deleteGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup) {
        Group group = groupRepository.findByIdGroup(idGroup);
        if (group == null)
            throw new NullPointerException("The group with idGroup " + idGroup + " does not exist.|uri=/api/v1/groups/" + idGroup);
        groupRepository.delete(group);
        return new ShowGroup(group).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @PostMapping("/{idGroup}/users/{idUser}")
    public Map<String, Object> addUser(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup,
                                       @PathVariable("idUser") @Min(value = 0, message = "The idUser must be positive.") Long idUser) {
        Group group = groupRepository.findByIdGroup(idGroup);
        if (group == null)
            throw new NullPointerException("The group with idGroup " + idGroup + " does not exist.|uri=/api/v1/groups/" + idGroup);
        User user = userRepository.findByIdUser(idUser);
        if (user == null)
            throw new NullPointerException("The user with idUser " + idUser + " does not exist.|uri=/api/v1/users/" + idUser);
        group.getUsers().add(user);
        groupRepository.save(group);
        return new ShowGroup(group).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @DeleteMapping("/{idGroup}/users/{idUser}")
    public Map<String, Object> deleteUser(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup,
                                          @PathVariable("idUser") @Min(value = 0, message = "The idUser must be positive.") Long idUser) {
        Group group = groupRepository.findByIdGroup(idGroup);
        if (group == null)
            throw new NullPointerException("The group with idGroup " + idGroup + " does not exist.|uri=/api/v1/groups/" + idGroup);
        User user = userRepository.findByIdUser(idUser);
        if (user == null)
            throw new NullPointerException("The user with idUser " + idUser + " does not exist.|uri=/api/v1/users/" + idUser);
        group.getUsers().remove(user);
        groupRepository.save(group);
        return new ShowGroup(group).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @PostMapping("/{idGroup}/tasks/{idTask}")
    public Map<String, Object> addTask(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup,
                                       @PathVariable("idTask") @Min(value = 0, message = "The idTask must be positive.") Long idTask) {
        Group group = groupRepository.findByIdGroup(idGroup);
        if (group == null)
            throw new NullPointerException("The group with idGroup " + idGroup + " does not exist.|uri=/api/v1/groups/" + idGroup);
        Task task = taskRepository.findByIdTask(idTask);
        if (task == null)
            throw new NullPointerException("The task with idTask " + idTask + " does not exist.|uri=/api/v1/tasks/" + idTask);
        for (User user : group.getUsers()) {
            user.getTasks().add(task);
            userRepository.save(user);
        }
        return new ShowGroup(group).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @DeleteMapping("/{idGroup}/tasks/{idTask}")
    public Map<String, Object> deleteTask(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup,
                                          @PathVariable("idTask") @Min(value = 0, message = "The idTask must be positive.") Long idTask) {
        Group group = groupRepository.findByIdGroup(idGroup);
        if (group == null)
            throw new NullPointerException("The group with idGroup " + idGroup + " does not exist.|uri=/api/v1/groups/" + idGroup);
        Task task = taskRepository.findByIdTask(idTask);
        if (task == null)
            throw new NullPointerException("The task with idTask " + idTask + " does not exist.|uri=/api/v1/tasks/" + idTask);
        for (User user : group.getUsers()) {
            user.getTasks().remove(task);
            userRepository.save(user);
        }
        return new ShowGroup(group).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }

    @DeleteMapping("/{idGroup}/users")
    public Map<String, Object> deleteAllUsers(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Long idGroup) {
        Group group = groupRepository.findByIdGroup(idGroup);
        if (group == null)
            throw new NullPointerException("The group with idGroup " + idGroup + " does not exist.|uri=/api/v1/groups/" + idGroup);
        group.getUsers().clear();
        groupRepository.save(group);
        return new ShowGroup(group).getFields(ShowGroup.ALL_ATTRIBUTES, ShowUser.ALL_ATTRIBUTES, ShowTask.ALL_ATTRIBUTES);
    }
}
