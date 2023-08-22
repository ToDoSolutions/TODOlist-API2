package com.todolist.controllers.group;

import com.fadda.common.Preconditions;
import com.todolist.component.DTOManager;
import com.todolist.dtos.Order;
import com.todolist.dtos.ShowGroup;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import com.todolist.entity.Group;
import com.todolist.exceptions.BadRequestException;
import com.todolist.filters.DateFilter;
import com.todolist.filters.NumberFilter;
import com.todolist.services.group.GroupService;
import com.todolist.services.group.GroupUserService;
import com.todolist.validators.FieldValidator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@RestController
@RequestMapping("/api/v1")
public class GroupController {

    // Services ---------------------------------------------------------------
    private final GroupService groupService;

    private final GroupUserService groupUserService;

    // Components --------------------------------------------------------------
    private final DTOManager dtoManager;

    // Validators --------------------------------------------------------------
    private final Consumer<String[]> fieldValidator;

    // Constructors -----------------------------------------------------------
    @Autowired
    public GroupController(GroupService groupService, DTOManager dtoManager, FieldValidator fieldValidator, GroupUserService groupUserService) {
        this.groupService = groupService;
        this.dtoManager = dtoManager;
        this.fieldValidator = fields -> {
            fieldValidator.taskFieldValidate(fields[0]);
            fieldValidator.userFieldValidate(fields[1]);
            fieldValidator.groupFieldValidate(fields[2]);
        };
        this.groupUserService = groupUserService;
    }

    /* ------------ */
    // CRUD Methods //
    /* ------------ */

    // Getters -----------------------------------------------------------------
    @GetMapping("/groups") // GetAllTest
    public List<Map<String, Object>> getAllGroups(@RequestParam(defaultValue = "0") @Min(value = 0, message = "The offset must be positive.") Integer offset,
                                                  @RequestParam(defaultValue = "-1") @Min(value = -1, message = "The limit must be positive.") Integer limit,
                                                  @RequestParam(defaultValue = "+idGroup") Order order,
                                                  @RequestParam(defaultValue = ShowGroup.ALL_ATTRIBUTES_STRING) String fieldsGroup,
                                                  @RequestParam(defaultValue = ShowUser.ALL_ATTRIBUTES_STRING) String fieldsUser,
                                                  @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES_STRING) String fieldsTask,
                                                  @RequestParam(required = false) String name,
                                                  @RequestParam(required = false) String description,
                                                  @RequestParam(required = false) NumberFilter numTasks,
                                                  @RequestParam(required = false) DateFilter createdDate) {
        order.validateOrder(fieldsGroup);
        List<Group> groups = groupService.findAllGroups(order.getSort());
        return groups.stream().skip(offset).limit(limit).filter(group -> Objects.nonNull(group) &&
                        Preconditions.isNullOrValid(name, n -> group.getName().equals(n)) &&
                        Preconditions.isNullOrValid(description, d -> group.getDescription().equals(d)) &&
                        Preconditions.isNullOrValid(numTasks, n -> n.isValid(groupService.getNumTasks(group))) &&
                        Preconditions.isNullOrValid(createdDate, c -> c.isValid(group.getCreatedDate())))
                .map(group -> new ShowGroup(group, groupUserService.getShowUsersFromGroup(group))).map(group -> dtoManager.getEntityAsJson(group,
                fieldValidator, fieldsTask, fieldsUser, fieldsGroup)).toList();
    }

    @GetMapping("/group/{idGroup}")
    public Map<String, Object> getGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Integer idGroup,
                                        @RequestParam(defaultValue = ShowGroup.ALL_ATTRIBUTES_STRING) String fieldsGroup,
                                        @RequestParam(defaultValue = ShowUser.ALL_ATTRIBUTES_STRING) String fieldsUser,
                                        @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES_STRING) String fieldsTask) {
        Group group = groupService.findGroupById(idGroup);
        ShowGroup showGroup = new ShowGroup(group, groupUserService.getShowUsersFromGroup(group));
        return dtoManager.getEntityAsJson(showGroup, fieldValidator, fieldsTask, fieldsUser, fieldsGroup);
    }

    // Adders ------------------------------------------------------------------
    @PostMapping("/group") // PostTest
    public ResponseEntity<ShowGroup> addGroup(@RequestBody @Valid Group group, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new BadRequestException(bindingResult.getAllErrors().get(0).getDefaultMessage());
        group = groupService.saveGroup(group);
        ShowGroup showGroup = new ShowGroup(group, groupUserService.getShowUsersFromGroup(group));
        return ResponseEntity.ok(showGroup);
    }

    // Updaters ----------------------------------------------------------------
    @PutMapping("/group") // PutTest
    public ResponseEntity<ShowGroup> updateGroup(@RequestBody @Valid Group group, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new BadRequestException(bindingResult.getAllErrors().get(0).getDefaultMessage());
        Group oldGroup = groupService.findGroupById(group.getId());
        BeanUtils.copyProperties(group, oldGroup, "idGroup", "createdDate");
        oldGroup = groupService.saveGroup(oldGroup);
        ShowGroup showGroup = new ShowGroup(group, groupUserService.getShowUsersFromGroup(oldGroup));
        return ResponseEntity.ok(showGroup);
    }

    // Deleters -----------------------------------------------------------------
    @DeleteMapping("/group/{idGroup}")
    public ResponseEntity<ShowGroup> deleteGroup(@PathVariable("idGroup") @Min(value = 0, message = "The idGroup must be positive.") Integer idGroup) {
        Group group = groupService.findGroupById(idGroup);
        groupService.deleteGroup(group);
        ShowGroup showGroup = new ShowGroup(group, groupUserService.getShowUsersFromGroup(group));
        return ResponseEntity.ok(showGroup);
    }
}
