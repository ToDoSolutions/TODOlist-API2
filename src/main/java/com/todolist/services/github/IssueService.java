package com.todolist.services.github;

import com.todolist.component.FetchApiData;
import com.todolist.entity.User;
import com.todolist.entity.autodoc.github.Issue;
import com.todolist.entity.autodoc.github.Owner;
import com.todolist.entity.autodoc.github.TaskGitHub;
import com.todolist.exceptions.NotFoundException;
import com.todolist.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class IssueService {

    private final RepoService repoService;
    private final UserService userService;
    private final FetchApiData fetchApiData;
    @Value("${github.api.url}")
    private String startUrl;

    @Autowired
    public IssueService(RepoService repoService, UserService userService, FetchApiData fetchApiData) {
        this.repoService = repoService;
        this.userService = userService;
        this.fetchApiData = fetchApiData;
    }


    // Get all issues from a repo
    public Issue[] findByUsernameAndRepo(String username, String repoName) {
        User user = userService.findUserByUsername(username);
        TaskGitHub task = repoService.findRepoByName(user.getUsername(), repoName);
        return Arrays.stream(fetchApiData.getApiData(startUrl + "/repos/" + user.getUsername() + "/" + task.getName() + "/issues?state=all&per_page=999", Issue[].class))
                .filter(issue -> !(issue.title.contains("ADD") || issue.title.contains("FIX"))).toArray(Issue[]::new);
    }

    public User getUserAssignedToIssue(Owner owner) {
        return userService.findUserByUsername(owner.getLogin());
    }
}
