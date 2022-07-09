package com.todolist.resources.github;

import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.entity.github.Owner;
import com.todolist.entity.github.TaskGitHub;
import com.todolist.repository.Repositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/github")
public class OwnerResource {

    @Autowired
    @Qualifier("repositories")
    private Repositories repositories;

    @GetMapping("user/{idUser}")
    public Map<String, Object> getOwner(
            @PathVariable long idUser,
            @RequestParam(defaultValue = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration") String fieldsTask,
            @RequestParam(defaultValue = "idUser,name,surname,email,avatar,bio,location,taskCompleted,tasks") String fieldsUser) {
        User oldUser = repositories.findUserById(idUser);
        if (oldUser == null) {
            throw new IllegalArgumentException("User not found");
        }
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
        User user = new User();
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
        String email = getAdditional(additional, "email");
        String bio = getAdditional(additional, "bio");
        String location = getAdditional(additional, "location");
        String avatar = getAdditional(additional, "avatar_url");
        String username = getAdditional(additional, "login");
        user.setAvatar(avatar);
        user.setBio(bio);
        user.setEmail(email);
        user.setIdUser(idUser);
        user.setLocation(location);
        user.setName(name);
        user.setSurname(surname);
        user.setUsername(username);
        return new ShowUser(user, repositories.getShowTaskFromUser(user)).getFields(fieldsUser, fieldsTask);
    }

    @PostMapping("user/{idUser}/task/{repoName}")
    public Map<String, Object> addTask(@PathVariable long idUser,
                                       @PathVariable String repoName,
                                       @RequestParam(required = false) @Pattern(regexp = "DRAFT|IN_PROGRESS|DONE|IN_REVISION|CANCELLED", message = "The status is invalid.") String status,
                                       @RequestParam(required = false) @Max(value=5, message = "The priority must be between 0 and 5.") Integer priority,
                                       @RequestParam(required = false) @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The finishedDate is invalid.") String finishedDate,
                                       @RequestParam(required = false) String annotation,
                                       @RequestParam(required = false) String difficulty) {
        User user = repositories.findUserById(idUser);
        if (user == null)
            throw new IllegalArgumentException("User not found");
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
        Task task = new Task();
        task.setTitle(repo.getName());
        task.setDescription(repo.getDescription());
        task.setStatus(status);
        task.setFinishedDate(finishedDate);
        task.setStartDate(repo.getCreatedAt().split("T")[0]);
        task.setPriority(priority);
        task.setAnnotation(annotation);
        task.setDifficulty(difficulty);
        task.setIdTask(-1);
        task = repositories.saveTask(task);
        return new ShowTask(task).getFields(ShowTask.ALL_ATTRIBUTES);
    }


    private static String getAdditional(Map<String, Object> additional, String key) {
        Object aux = additional.get(key);
        return aux == null ? null : aux.toString();
    }




}
