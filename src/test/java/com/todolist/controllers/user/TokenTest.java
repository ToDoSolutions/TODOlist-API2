package com.todolist.controllers.user;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.TODOlistApplication;
import com.todolist.dtos.ShowUser;
import com.todolist.entity.User;
import com.todolist.entity.github.Owner;
import com.todolist.exceptions.ManagerException;
import com.todolist.exceptions.NotFoundException;
import com.todolist.repositories.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@FlywayTest(additionalLocations = "db/migrate", value = @DataSource(url = "jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
// @FlywayTest(additionalLocations = "db/migrate", value = @DataSource(url = "jdbc:mysql://uqiweqtspt5rb4xp:uWHt8scUWIMHRDzt7HCg@b8iyr7xai8wk75ismpbt-mysql.services.clever-cloud.com:3306/b8iyr7xai8wk75ismpbt", username = "uqiweqtspt5rb4xp", password = "uWHt8scUWIMHRDzt7HCg"))
public class TokenTest {

    String uri = "http://localhost:8080/api/v1";
    // String uri = "https://todolist-api2.herokuapp.com/api/v1";
    RestTemplate restTemplate;

    @BeforeAll
    public static void beforeClass() {
        TODOlistApplication.main(new String[]{});
    }

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
    }

    @Test
    void testTokenFine() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "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");
        System.out.println(headers);
        ShowUser response = restTemplate.exchange(uri + "/user/7/token", HttpMethod.PUT, new HttpEntity<>(null, headers), ShowUser.class).getBody();
        // Comprobar por HeidySQL, por los test no se puede acceder a los tokens.
    }

    @Test
    void testTokenWrong() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "pepe");
        ManagerException.of(assertThrows(HttpClientErrorException.class, ()->restTemplate.exchange(uri + "/user/7/token", HttpMethod.PUT, new HttpEntity<>(null, headers), ShowUser.class)))
                .assertMsg("The token is invalid.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/user/7/token");
    }

    @Test
    void testTokenNull() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", null);
        ManagerException.of(assertThrows(HttpClientErrorException.class, ()->restTemplate.exchange(uri + "/user/7/token", HttpMethod.PUT, new HttpEntity<>(null, headers), ShowUser.class)))
                .assertMsg("The token is required.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/user/7/token");
    }

    @Test
    void testTokenEmpty() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "");
        System.out.println(headers);
        ManagerException.of(assertThrows(HttpClientErrorException.class, ()->restTemplate.exchange(uri + "/user/7/token", HttpMethod.PUT, new HttpEntity<>(null, headers), ShowUser.class)))
                .assertMsg("The token is required.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/user/7/token");
    }
}
