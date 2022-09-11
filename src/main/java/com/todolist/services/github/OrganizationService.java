package com.todolist.services.github;

import com.todolist.dtos.ShowGroup;
import com.todolist.entity.Group;
import com.todolist.entity.github.Organization;
import com.todolist.entity.github.Repo;
import com.todolist.exceptions.NotFoundException;
import com.todolist.services.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class OrganizationService {

    @Value("${github.api.url}")
    private String startUrl;

    @Autowired
    private GroupService groupService;

    // findById()
    public Organization findById(Long idGroup) {
        Group group = groupService.findGroupById(idGroup);
        if (group == null)
            throw new NotFoundException("Group not found");
        String url = startUrl + "/orgs/" + group.getName();
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, Organization.class);
    }

    public Group turnOrganizationIntoGroup(Organization organization) {
        return Group.of(organization.getLogin(), organization.getDescription(), organization.getCreatedAt());
    }

    public ShowGroup turnOrganizationIntoShowGroup(Organization organization) {
        Group group = turnOrganizationIntoGroup(organization);
        return new ShowGroup(group, groupService.getShowUserFromGroup(group));
    }
    // Update an organization -> https://api.github.com/orgs/:org

    // Get org from an user /users/{username}/orgs


    // Get the repos from an organization orgs/{org} -> repos_url

    // Añadir colaboradores de un proyecto/repos/{owner}/{repo}/collaborators
    // Añadir Y eliminar colaborador /repos/{owner}/{repo}/collaborators/{username}
}
