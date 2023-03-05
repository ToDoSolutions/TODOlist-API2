package com.todolist.services.github;

import com.todolist.component.FetchApiData;
import com.todolist.entity.Group;
import com.todolist.entity.autodoc.github.Organization;
import com.todolist.services.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class OrganizationService {

    private final GroupService groupService;
    private final FetchApiData fetchApiData;
    @Value("${github.api.url}")
    private String startUrl;
    @Value("${github.api.url.orgs}")
    private String orgsUrl;

    @Autowired
    public OrganizationService(GroupService groupService, FetchApiData fetchApiData) {
        this.groupService = groupService;
        this.fetchApiData = fetchApiData;
    }


    // findById()
    public Organization findById(Long idGroup) {
        Group group = groupService.findGroupById(idGroup);
        String url = orgsUrl.replace("{groupName}", group.getName());
        return fetchApiData.getApiData(url, Organization.class);
    }
    // Update an organization -> https://api.github.com/orgs/:org

    // Get org from an user /users/{username}/orgs


    // Get the repos from an organization orgs/{org} -> repos_url

    // Añadir colaboradores de un proyecto/repos/{owner}/{repo}/collaborators
    // Añadir Y eliminar colaborador /repos/{owner}/{repo}/collaborators/{username}
}
