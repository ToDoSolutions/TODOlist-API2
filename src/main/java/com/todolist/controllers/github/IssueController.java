package com.todolist.controllers.github;

import com.todolist.entity.autodoc.github.Issue;
import com.todolist.services.github.IssueService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/github")
@Validated
@AllArgsConstructor
public class IssueController {

    private final IssueService issueService;

    @GetMapping("/issues/{username}/{repoName}")
    public Issue[] getIssues(@PathVariable String username, @PathVariable String repoName) {
        return issueService.findByUsernameAndRepo(username, repoName);
    }

    @GetMapping("/issues/{username}/{repoName}/md")
    public Issue[] getIssuesWithMarkdown(@PathVariable String username, @PathVariable String repoName) {
        return issueService.findByUsernameAndRepo(username, repoName);
    }
}
