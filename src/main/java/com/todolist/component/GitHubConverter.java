package com.todolist.component;

import com.todolist.dtos.Difficulty;
import com.todolist.dtos.Status;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.entity.autodoc.github.Owner;
import com.todolist.entity.autodoc.github.Release;
import com.todolist.entity.autodoc.github.Repo;
import com.todolist.entity.autodoc.github.TaskGitHub;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class GitHubConverter {

    /* REPO */
    public Task turnTaskGitHubIntoTask(TaskGitHub taskGitHub, LocalDate finishedDate, Long priority, Difficulty difficulty) {
        return Task.of(
                taskGitHub.getName(),
                taskGitHub.getDescription(),
                taskGitHub.getCloneUrl(),
                getStatus(taskGitHub.getReleasesUrl()),
                finishedDate,
                LocalDate.parse(taskGitHub.getCreatedAt().split("T")[0]), priority, difficulty);
    }

    private Status getStatus(String releaseUrl) {
        RestTemplate restTemplate = new RestTemplate();
        String url = releaseUrl + "/latest"; // Solo funciona si se pasa el token.
        Release release;
        try {
            release = restTemplate.getForObject(url, Release.class);
        } catch (Exception e) {
            return Status.UNKNOWN;
        }
        if (release == null) return Status.CANCELLED;
        else if (Boolean.TRUE.equals(release.getDraft())) return Status.DRAFT;
        else if (Boolean.TRUE.equals(release.getPrerelease())) return Status.DONE;
        else return Status.IN_REVISION;
    }

    public Repo turnTaskIntoRepo(
            Task task, Boolean haveAutoInit, Boolean isPrivate, String gitIgnoreTemplate, Boolean isTemplate, String homepage) {
        Repo repo = new Repo();
        repo.setName(task.getTitle());
        repo.setDescription(task.getDescription());
        repo.setAutoInit(haveAutoInit);
        repo.setPrivate(isPrivate);
        repo.setGitignoreTemplate(gitIgnoreTemplate);
        repo.setTemplate(isTemplate);
        repo.setHomepage(homepage);
        return repo;
    }

    /* OWNER */
    public User turnOwnerIntoUser(Owner owner, String password) {
        Object auxName = owner.getName();
        List<String> fullName;
        String name = null;
        String surname = null;
        if (auxName != null) {
            fullName = Arrays.asList(owner.getName().split(" "));
            name = fullName.get(0);
            surname = fullName.size() == 1 ? null : fullName.stream().skip(1).reduce("", (ac, nx) -> ac + " " + nx);
        }
        return User.of(name, surname, owner.getLogin(), owner.getEmail(), owner.getAvatarUrl(), owner.getBio(), owner.getLocation(), password);
    }
}
