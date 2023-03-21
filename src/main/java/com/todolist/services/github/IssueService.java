package com.todolist.services.github;

import com.todolist.component.FetchApiData;
import com.todolist.dtos.autodoc.github.Issue;
import com.todolist.dtos.autodoc.github.Owner;
import com.todolist.dtos.autodoc.github.TaskGitHub;
import com.todolist.entity.User;
import com.todolist.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class IssueService {

    // Services ---------------------------------------------------------------
    private final RepoService repoService;
    private final UserService userService;
    private final FetchApiData fetchApiData;

    // Urls -------------------------------------------------------------------
    @Value("${github.api.url}")
    private String startUrl;

    // Constructors -----------------------------------------------------------
    @Autowired
    public IssueService(RepoService repoService, UserService userService, FetchApiData fetchApiData) {
        this.repoService = repoService;
        this.userService = userService;
        this.fetchApiData = fetchApiData;
    }

    // Methods ----------------------------------------------------------------
    public Issue[] findByUsernameAndRepo(String username, String repoName) { // Get all issues from a repo
        User user = userService.findUserByUsername(username);
        TaskGitHub task = repoService.findRepoByName(user.getUsername(), repoName);
        return Arrays.stream(fetchApiData.getApiData(startUrl + "/repos/" + user.getUsername() + "/" + task.getName() + "/issues?state=all&per_page=999", Issue[].class))
                .filter(issue -> !(issue.getTitle().contains("ADD") || issue.getTitle().contains("FIX"))).toArray(Issue[]::new);
    }

    public User getUserAssignedToIssue(Owner owner) {
        return userService.findUserByUsername(owner.getLogin());
    }
}
