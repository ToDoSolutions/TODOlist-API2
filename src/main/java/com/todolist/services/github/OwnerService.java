package com.todolist.services.github;

import com.todolist.dtos.ShowUser;
import com.todolist.entity.User;
import com.todolist.entity.github.Owner;
import com.todolist.exceptions.NotFoundException;
import com.todolist.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class OwnerService {

    @Value("${github.api.url}")
    private String startUrl;

    @Autowired
    private UserService userService;

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
        } else owner = restTemplate.getForObject(url, Owner.class);
        return owner;
    }

    public ShowUser turnOwnerIntoShowUser(Owner owner) {
        User user = turnOwnerIntoUser(owner, "pwd");
        return new ShowUser(user, userService.getShowTaskFromUser(user));
    }

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

    public User updateUser(User oldUser) {
        Owner owner = findById(oldUser.getIdUser());
        User newUser = turnOwnerIntoUser(owner, oldUser.getPassword());
        oldUser = userService.updateUser(oldUser, newUser);
        return oldUser;
    }

    public Owner updateOwner(User oldUser) {
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
        owner = restTemplate.postForEntity(url, new HttpEntity<>(owner, headers), Owner.class, headers).getBody();
        return owner;
    }

    // List organizations for user
    // Authneticated -> https://api.github.com/user/orgs
    // Unauthenticated -> https://api.github.com/users/:username/orgs
    // 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
}
