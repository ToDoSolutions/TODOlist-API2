package com.todolist.controllers.github;

import org.springframework.beans.factory.annotation.Value;

public class OrganizationController {

    @Value("${github.api.url}")
    private String startUrl;

    // Get an organization orgs/{org}

    // Get org from an user /users/{username}/orgs

    // Get the repos from an organization orgs/{org} -> repos_url
}
