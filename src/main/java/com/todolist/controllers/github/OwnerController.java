package com.todolist.controllers.github;

import com.todolist.dtos.ShowUser;
import com.todolist.entity.User;
import com.todolist.entity.github.Owner;
import com.todolist.exceptions.NotFoundException;
import com.todolist.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/github")
public class OwnerController {

    @Value("${github.api.url}")
    private String startUrl;

    @Autowired
    private UserService userService;

    private static String getAdditional(Map<String, Object> additional, String key) {
        Object aux = additional.get(key);
        return aux == null ? null : aux.toString();
    }

    // Obtener usuario de GitHub (ya existente)
    @GetMapping("user/{idUser}")
    public ShowUser getOwner(@PathVariable long idUser) {
        User oldUser = userService.findUserById(idUser);
        if (oldUser == null)
            throw new NotFoundException("User not found");
        String url = startUrl + "/users/" + oldUser.getUsername();
        RestTemplate restTemplate = new RestTemplate();
        Owner owner;
        if (oldUser.getToken() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + oldUser.getToken());
            owner = restTemplate.getForObject(url, Owner.class, headers);
        } else {
            owner = restTemplate.getForObject(url, Owner.class);
        }
        Map<String, Object> additional = owner.getAdditionalProperties();
        Object auxName = additional.get("name");
        List<String> fullName;
        String name = null;
        String surname = null;
        if (auxName != null) {
            fullName = Arrays.asList(additional.get("name").toString().split(" "));
            name = fullName.get(0);
            surname = fullName.size() == 1 ? null : fullName.stream().skip(1).reduce("", (ac, nx) -> ac + " " + nx);
        }
        User user = User.of(name, surname, getAdditional(additional, "login"), getAdditional(additional, "avatar_url"), getAdditional(additional, "email"), getAdditional(additional, "bio"), getAdditional(additional, "location"), "pwd");
        return new ShowUser(user, userService.getShowTaskFromUser(user));
    }

    // Sabir tareas a GitHub para un usuario ya existente, pedir password
    // Mover a repo.

    // Actualizar usuario.

    // Añadir colaboradores de un proyecto/repos/{owner}/{repo}/collaborators
    // Añadir Y eliminar colaborador /repos/{owner}/{repo}/collaborators/{username}
}
