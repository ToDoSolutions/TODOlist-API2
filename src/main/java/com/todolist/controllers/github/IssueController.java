package com.todolist.controllers.github;

import com.todolist.component.MarkdownAdministrator;
import com.todolist.entity.github.Issue;
import com.todolist.services.github.IssueService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    private final MarkdownAdministrator markdownAdministrator;

    @GetMapping("/issues/{username}/{repoName}")
    public ResponseEntity<Issue[]> getIssues(@PathVariable String username, @PathVariable String repoName) {
        Issue[] issues = issueService.findByUsernameAndRepo(username, repoName);

        return ResponseEntity.ok().body(issues);
    }
}
