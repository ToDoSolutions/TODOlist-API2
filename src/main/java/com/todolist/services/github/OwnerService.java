package com.todolist.services.github;

import com.todolist.dtos.ShowUser;
import com.todolist.entity.User;
import com.todolist.entity.github.Owner;
import com.todolist.exceptions.BadRequestException;
import com.todolist.exceptions.NotFoundException;
import com.todolist.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class OwnerService {

    @Value("${github.api.url}")
    private String startUrl;

    @Autowired
    private UserService userService;

    private static String getAdditional(Map<String, Object> additional, String key) {
        Object aux = additional.get(key);
        return aux == null ? null : aux.toString();
    }

    // findById(String userId)
    public Owner findById(Long idUser) {
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
        return owner;
    }

    public ShowUser turnOwnerIntoShowTask(Owner owner) {
        User user = turnOwnerIntoTask(owner, "pwd");
        return new ShowUser(user, userService.getShowTaskFromUser(user));
    }

    public User turnOwnerIntoTask(Owner owner, String password) {
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
        return User.of(name, surname, getAdditional(additional, "login"), getAdditional(additional, "email"), getAdditional(additional, "avatar_url"), getAdditional(additional, "bio"), getAdditional(additional, "location"), password);
    }

    public User updateUser(User newUser) {
        Owner owner = findById(newUser.getIdUser());
        return turnOwnerIntoTask(owner, newUser.getPassword());
    }

    public Owner updateOwner(Long idUser) {
        User oldUser = userService.findUserById(idUser);
        if (oldUser == null) throw new NotFoundException("User not found");
        if (oldUser.getToken() == null) throw new BadRequestException("The token is needed");
        Owner owner = findById(oldUser.getIdUser());
        if (oldUser.getUsername() != null) owner.setLogin(oldUser.getUsername());
        if (oldUser.getAvatar() != null) owner.setAvatarUrl(oldUser.getAvatar());
        if (oldUser.getEmail() != null) owner.setEmail(oldUser.getEmail());
        if (oldUser.getBio() != null) owner.setBio(oldUser.getBio());
        if (oldUser.getLocation() != null) owner.setLocation(oldUser.getLocation());
        String url = startUrl + "/users/" + oldUser.getUsername();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + oldUser.getToken());
        owner = restTemplate.postForEntity(url, owner, Owner.class, headers).getBody();
        return owner;
    }
}
