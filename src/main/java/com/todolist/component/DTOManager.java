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

    private final UserService userService;
    private final GroupService groupService;
    private final FieldValidator fieldValidator;

    @Autowired
    public DTOManager(UserService userService, GroupService groupService, FieldValidator fieldValidator) {
        this.userService = userService;
        this.groupService = groupService;
        this.fieldValidator = fieldValidator;
    }

    public ShowTask getShowTask(Task task) {
        return new ShowTask(task);
    }

    public Map<String, Object> getShowTaskAsJson(Task task, String fieldsTask) {
        fieldValidator.taskFieldValidate(fieldsTask);
        return getShowTask(task).getFields(fieldsTask);
    }

    public Map<String, Object> getShowTaskAsJson(Task task) {
        return getShowTask(task).getFields();
    }

    public Map<String, Object> getShowTaskAsJsonWithOutTimes(Task task, String fieldsTask) {
        return getShowTask(task).getFields(fieldsTask.replace("finishedDate", "").replace("priority", "").replace("difficulty", "").replace("duration", "").replace("idTask", ""));
    }

    public ShowUser getShowUser(User user) {
        return new ShowUser(user, userService.getShowTaskFromUser(user));
    }

    public Map<String, Object> getShowUserAsJson(User user, String fieldsUser, String fieldsTask) {
        fieldValidator.userFieldValidate(fieldsUser);
        fieldValidator.taskFieldValidate(fieldsTask);
        return getShowUser(user).getFields(fieldsUser, fieldsTask);
    }

    public Map<String, Object> getShowUserAsJson(User user, String fieldsUser) {
        fieldValidator.userFieldValidate(fieldsUser);
        return getShowUser(user).getFields(fieldsUser);
    }

    public Map<String, Object> getShowUserAsJson(User user) {
        return getShowUser(user).getFields();
    }

    public ShowGroup getShowGroup(Group group) {
        return new ShowGroup(group, groupService.getShowUserFromGroup(group));
    }

    public Map<String, Object> getShowGroupAsJson(Group group, String fieldsGroup, String fieldsUser, String fieldsTask) {
        fieldValidator.groupFieldValidate(fieldsGroup);
        fieldValidator.userFieldValidate(fieldsUser);
        fieldValidator.taskFieldValidate(fieldsTask);
        return getShowGroup(group).getFields(fieldsGroup, fieldsUser, fieldsTask);
    }

    public Map<String, Object> getShowGroupAsJson(Group group, String fieldsGroup, String fieldsUser) {
        fieldValidator.groupFieldValidate(fieldsGroup);
        fieldValidator.userFieldValidate(fieldsUser);
        return getShowGroup(group).getFields(fieldsGroup, fieldsUser);
    }

    public Map<String, Object> getShowGroupAsJson(Group group, String fieldsGroup) {
        fieldValidator.groupFieldValidate(fieldsGroup);
        return getShowGroup(group).getFields(fieldsGroup);
    }

    public Map<String, Object> getShowGroupAsJson(Group group) {
        return getShowGroup(group).getFields();
    }


}
