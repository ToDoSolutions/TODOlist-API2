package com.todolist.services.github;

import com.todolist.component.FetchApiData;
import com.todolist.component.GitHubConverter;
import com.todolist.entity.User;
import com.todolist.entity.autodoc.github.Owner;
import com.todolist.services.UserService;
import org.javatuples.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OwnerService {

    public static final String EASY_PASSWORD = "1234";
    public static final String USERNAME = "{username}";
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String[] IGNORED_PROPERTIES = {"id", "password", "tasks", "token"};
    private final UserService userService;
    private final FetchApiData fetchApiData;
    private final GitHubConverter gitHubConverter;
    @Value("${github.api.url}")
    private String startUrl;
    @Value("${github.api.url.user}")
    private String userUrl;


    @Autowired
    public OwnerService(UserService userService, FetchApiData fetchApiData, GitHubConverter gitHubConverter) {
        this.userService = userService;
        this.fetchApiData = fetchApiData;
        this.gitHubConverter = gitHubConverter;
    }

    // findById(String userId)
    public Owner findByUsername(String username) {
        User oldUser = userService.findUserByUsername(username);
        String url = userUrl.replace(USERNAME, username);
        return fetchApiData.getApiDataWithToken(url, Owner.class, new Pair<>(AUTHORIZATION, BEARER + oldUser.getToken()));
    }

    @Transactional
    public User updateUser(User oldUser) {
        Owner owner = findByUsername(oldUser.getUsername());
        User newUser = gitHubConverter.turnOwnerIntoUser(owner, oldUser.getPassword());
        BeanUtils.copyProperties(newUser, oldUser, IGNORED_PROPERTIES);
        oldUser = userService.saveUser(oldUser);
        return oldUser;
    }

    public Owner updateOwner(User oldUser) {
        Owner owner = findByUsername(oldUser.getUsername());
        BeanUtils.copyProperties(oldUser, owner, IGNORED_PROPERTIES);
        String url = userUrl.replace(USERNAME, oldUser.getUsername());
        return fetchApiData.postApiDataWithToken(url, Owner.class, new Pair<>(AUTHORIZATION, BEARER + oldUser.getToken()), owner);
    }

    // List organizations for user
    // Authneticated -> https://api.github.com/user/orgs
    // Unauthenticated -> https://api.github.com/users/:username/orgs
    // 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
}
