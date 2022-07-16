package com.todolist.controllers.github;

import com.google.common.base.Preconditions;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.entity.github.Owner;
import com.todolist.entity.github.TaskGitHub;
import com.todolist.services.TaskService;
import com.todolist.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/github")
@AllArgsConstructor
public class OwnerController {

    private TaskService taskService;

    private UserService userService;

    private static String getAdditional(Map<String, Object> additional, String key) {
        Object aux = additional.get(key);
        return aux == null ? null : aux.toString();
    }

    @GetMapping("user/{idUser}")
    public Map<String, Object> getOwner(
            @PathVariable long idUser,
            @RequestParam(defaultValue = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration") String fieldsTask,
            @RequestParam(defaultValue = "idUser,name,surname,email,avatar,bio,location,taskCompleted,tasks") String fieldsUser) {
        User oldUser = userService.findUserById(idUser);
        Preconditions.checkNotNull(oldUser, "User not found");
        String uri = "https://api.github.com/users/" + oldUser.getUsername();
        RestTemplate restTemplate = new RestTemplate();
        Owner owner;
        if (oldUser.getToken() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + oldUser.getToken());
            owner = restTemplate.getForObject(uri, Owner.class, headers);
        } else {
            owner = restTemplate.getForObject(uri, Owner.class);
        }
        Map<String, Object> additional = owner.getAdditionalProperties();
        Object auxName = additional.get("name");
        List<String> fullName;
        String name = null;
        String surname = null;
        if (auxName != null) {
            fullName = Arrays.asList(additional.get("name").toString().split(" "));
            name = fullName.get(0);
            surname = fullName.size() == 1 ? null : fullName.stream().skip(1).reduce("", (ac, nx) -> ac + " " + nx);
        }
        User user = User.of(name, surname, getAdditional(additional, "login"), getAdditional(additional, "avatar_url"), getAdditional(additional, "email"), getAdditional(additional, "bio"), getAdditional(additional, "location"), "pwd", "token");
        return new ShowUser(user, userService.getShowTaskFromUser(user)).getFields(fieldsUser, fieldsTask);
    }

    @PostMapping("user/{idUser}/task/{repoName}")
    public Map<String, Object> addTask(@PathVariable long idUser,
                                       @PathVariable String repoName,
                                       @RequestParam(required = false) @Pattern(regexp = "DRAFT|IN_PROGRESS|DONE|IN_REVISION|CANCELLED", message = "The status is invalid.") String status,
                                       @RequestParam(required = false) @Max(value = 5, message = "The priority must be between 0 and 5.") Long priority,
                                       @RequestParam(required = false) @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The finishedDate is invalid.") String finishedDate,
                                       @RequestParam(required = false) String annotation,
                                       @RequestParam(required = false) String difficulty) {
        User user = userService.findUserById(idUser);
        Preconditions.checkNotNull(user, "User not found");
        String uri = "https://api.github.com/users/" + user.getUsername() + "/repos/" + repoName;
        RestTemplate restTemplate = new RestTemplate();
        TaskGitHub repo;
        if (user.getToken() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + user.getToken());
            repo = restTemplate.getForObject(uri, TaskGitHub.class, headers);
        } else {
            repo = restTemplate.getForObject(uri, TaskGitHub.class);
        }
        Task task = Task.of(repo.getName(), repo.getDescription(), annotation, status, finishedDate, repo.getCreatedAt().split("T")[0], priority, difficulty);
        task = taskService.saveTask(task);
        return new ShowTask(task).getFields(ShowTask.ALL_ATTRIBUTES);
    }
}
