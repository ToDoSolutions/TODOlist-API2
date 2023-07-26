package com.todolist.dtos.autodoc;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Request {
    private String repoName;
    private String username;
    private String individual;

    private Area area;

    public String getPath() {
        return username + "/" + repoName;
    }

    public Boolean isIndividual() {
        return individual != null && !individual.isEmpty();
    }
}