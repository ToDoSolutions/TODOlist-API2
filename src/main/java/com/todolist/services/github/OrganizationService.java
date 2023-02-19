package com.todolist.services.github;

import com.todolist.component.FetchApiData;
import com.todolist.dtos.ShowGroup;
import com.todolist.entity.Group;
import com.todolist.entity.github.Organization;
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


    private final GroupService groupService;
    private final FetchApiData fetchApiData;

    @Autowired
    public OrganizationService(GroupService groupService, FetchApiData fetchApiData) {
        this.groupService = groupService;
        this.fetchApiData = fetchApiData;
    }


    // findById()
    public Organization findById(Long idGroup) {
        Group group = groupService.findGroupById(idGroup);
        if (group == null)
            throw new NotFoundException("Group not found");
        return fetchApiData.getApiData(startUrl + "/orgs/" + group.getName(), Organization.class);
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
