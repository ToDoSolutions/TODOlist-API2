package com.todolist.component;

import com.todolist.dtos.ShowGroup;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import com.todolist.entity.Group;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.services.GroupService;
import com.todolist.services.UserService;
import com.todolist.validators.FieldValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DTOManager {

    // Services ---------------------------------------------------------------
    private final UserService userService;
    private final GroupService groupService;

    // Components -------------------------------------------------------------
    private final FieldValidator fieldValidator;

    // Constructors -----------------------------------------------------------
    @Autowired
    public DTOManager(UserService userService, GroupService groupService, FieldValidator fieldValidator) {
        this.userService = userService;
        this.groupService = groupService;
        this.fieldValidator = fieldValidator;
    }

    // Methods ----------------------------------------------------------------
    public ShowTask getShowTask(Task task) {
        return new ShowTask(task);
    }

    public Map<String, Object> getShowTaskAsJson(Task task, String fieldsTask) {
        fieldValidator.taskFieldValidate(fieldsTask);
        return getShowTask(task).toJson(fieldsTask);
    }

    public Map<String, Object> getShowTaskAsJson(Task task) {
        return getShowTask(task).toJson();
    }

    public Map<String, Object> getShowTaskAsJsonWithOutTimes(Task task, String fieldsTask) {
        return getShowTask(task).toJson(fieldsTask.replace("finishedDate", "").replace("priority", "").replace("difficulty", "").replace("duration", "").replace("idTask", ""));
    }

    public ShowUser getShowUser(User user) {
        return new ShowUser(user, userService.getShowTaskFromUser(user));
    }

    public Map<String, Object> getShowUserAsJson(User user, String fieldsUser, String fieldsTask) {
        fieldValidator.userFieldValidate(fieldsUser);
        fieldValidator.taskFieldValidate(fieldsTask);
        return getShowUser(user).toJson(fieldsUser, fieldsTask);
    }

    public Map<String, Object> getShowUserAsJson(User user, String fieldsUser) {
        fieldValidator.userFieldValidate(fieldsUser);
        return getShowUser(user).toJson(fieldsUser);
    }

    public Map<String, Object> getShowUserAsJson(User user) {
        return getShowUser(user).toJson();
    }

    public ShowGroup getShowGroup(Group group) {
        return new ShowGroup(group, groupService.getShowUserFromGroup(group));
    }

    public Map<String, Object> getShowGroupAsJson(Group group, String fieldsGroup, String fieldsUser, String fieldsTask) {
        fieldValidator.groupFieldValidate(fieldsGroup);
        fieldValidator.userFieldValidate(fieldsUser);
        fieldValidator.taskFieldValidate(fieldsTask);
        return getShowGroup(group).toJson(fieldsGroup, fieldsUser, fieldsTask);
    }

    public Map<String, Object> getShowGroupAsJson(Group group, String fieldsGroup, String fieldsUser) {
        fieldValidator.groupFieldValidate(fieldsGroup);
        fieldValidator.userFieldValidate(fieldsUser);
        return getShowGroup(group).toJson(fieldsGroup, fieldsUser);
    }

    public Map<String, Object> getShowGroupAsJson(Group group, String fieldsGroup) {
        fieldValidator.groupFieldValidate(fieldsGroup);
        return getShowGroup(group).toJson(fieldsGroup);
    }

    public Map<String, Object> getShowGroupAsJson(Group group) {
        return getShowGroup(group).toJson();
    }


}
