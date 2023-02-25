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

@Service
public class IssueService {

    @Value("${github.api.url}")
    private String startUrl;

    private final RepoService repoService;
    private final UserService userService;
    private final FetchApiData fetchApiData;

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
        if (task == null)
            throw new NotFoundException("Task not found");
        return fetchApiData.getApiData(startUrl + "/repos/" + user.getUsername() + "/" + task.getName() + "/issues?state=all", Issue[].class);
    }

    public User getUserAssignedToIssue(Owner owner) {
        return userService.findUserByUsername(owner.getLogin());
    }
}
