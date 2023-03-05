package com.todolist.controllers.github;

import com.todolist.dtos.ShowGroup;
import com.todolist.entity.autodoc.github.Organization;
import com.todolist.services.github.OrganizationService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Si tiene pocas operaciones es debido a que tengo poca experiencia (por no decir ninguna) con las organizaciones de github.
@RestController
@RequestMapping("/api/v1/github")
@Validated
@AllArgsConstructor
public class OrganizationController {

    private OrganizationService organizationService;

    // Get an organization orgs/{org}
    @GetMapping("/orgs/{idGroup}")
    public Organization getOrganization(@PathVariable Long idGroup) {
        return organizationService.findById(idGroup);
    }
}
