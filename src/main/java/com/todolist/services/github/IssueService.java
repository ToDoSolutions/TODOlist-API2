package com.todolist.services.github;

import com.todolist.entity.User;
import com.todolist.entity.github.Issue;
import com.todolist.entity.github.TaskGitHub;
import com.todolist.exceptions.NotFoundException;
import com.todolist.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class IssueService {

    @Value("${github.api.url}")
    private String startUrl;

    private final RepoService repoService;
    private final UserService userService;


    // Get all issues from a repo
    public Issue[] findByUsernameAndRepo(String username, String repoName) {
        User user = userService.findUserByName(username);
        TaskGitHub task = repoService.findRepoByName(user.getIdUser(), repoName);
        if (task == null)
            throw new NotFoundException("Task not found");
        String url = startUrl + "/repos/" + user.getName() + "/" + task.getName() + "/issues";
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, Issue[].class);
    }
}
