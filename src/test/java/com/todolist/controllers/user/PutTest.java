package com.todolist.controllers.user;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.TODOlistApplication;
import com.todolist.dtos.ShowUser;
import com.todolist.entity.User;
import com.todolist.exceptions.ManagerException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@FlywayTest(additionalLocations = "db/testWithOutData", value = @DataSource(url = "jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
// @FlywayTest(additionalLocations = "db/testWithOutData", value = @DataSource(url = "jdbc:mysql://uqiweqtspt5rb4xp:uWHt8scUWIMHRDzt7HCg@b8iyr7xai8wk75ismpbt-mysql.services.clever-cloud.com:3306/b8iyr7xai8wk75ismpbt", username = "uqiweqtspt5rb4xp", password = "uWHt8scUWIMHRDzt7HCg"))
class PutTest {

    User user;
    ShowUser showUser;
    String uri = "http://localhost:8080/api/v1";
    // String uri = "https://todolist-api2.herokuapp.com/api/v1";
    RestTemplate restTemplate;

    @BeforeAll
    public static void beforeClass() {
        TODOlistApplication.main(new String[]{});
    }

    @BeforeEach
    void setUp() {
        user = User.of("User 1", "User 1 surname", "User 1 username", "user@todolist.com", "https://avatar.com/user1", "User 1 bio",
                "User 1 location", "User 1 password");
        restTemplate = new RestTemplate();
        showUser = restTemplate.postForObject(uri + "/users", user, ShowUser.class);
        user.setIdUser(showUser.getIdUser());
    }

    // Correct
    @Test
    void testPutFine() {
        RestTemplate restTemplate = new RestTemplate();
        user = User.of("User 2", "User 2 surname", "User 2 username", "user2@todolist.com", "https://avatar.com/user2", "User 2 bio",
                "User 2 location", "User 1 password");
        user.setIdUser(showUser.getIdUser());
        ShowUser response = restTemplate.exchange(uri + "/users", HttpMethod.PUT, new HttpEntity<>(user), ShowUser.class).getBody();
        assertEquals(1, response.getIdUser(), "IdUser is not correct");
        response = restTemplate.getForObject(uri + "/users/1", ShowUser.class);
        assertEquals(1, response.getIdUser(), "IdUser is not correct");
    }

    // Not exist
    @Test
    void testPutNotExist() {
        user.setIdUser(99L);
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/users", HttpMethod.PUT, new HttpEntity<>(user), ShowUser.class)))
                .assertMsg("The user with idUser 99 does not exist.")
                .assertStatus("Not Found")
                .assertPath("/api/v1/users");
    }

    // Name
    @Test
    void testPutWithNullOrEmptyName() {
        user.setName(null);
        ShowUser response = restTemplate.exchange(uri + "/users", HttpMethod.PUT, new HttpEntity<>(user), ShowUser.class).getBody();
        assertNotNull(response.getName(), "Name is null.");
        user.setName("");
        response = restTemplate.exchange(uri + "/users", HttpMethod.PUT, new HttpEntity<>(user), ShowUser.class).getBody();
        assertNotNull(response.getName(), "Name is empty.");
    }

    @Test
    void testPutWithTitleGreaterThan50() {
        user.setName(new String(new char[51]).replace("\0", "a"));
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/users", HttpMethod.PUT, new HttpEntity<>(user), ShowUser.class)))
                .assertMsg("The name is too long.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/users");
    }

    // Surname
    @Test
    void testPutWithNullOrEmptySurname() {
        user.setSurname(null);
        ShowUser response = restTemplate.exchange(uri + "/users", HttpMethod.PUT, new HttpEntity<>(user), ShowUser.class).getBody();
        assertNotNull(response.getSurname(), "Surname is null.");
        user.setSurname("");
        response = restTemplate.exchange(uri + "/users", HttpMethod.PUT, new HttpEntity<>(user), ShowUser.class).getBody();
        assertNotNull(response.getSurname(), "Surname is empty.");
    }

    @Test
    void testPuttWithSurnameGreaterThan50() {
        user.setSurname(new String(new char[51]).replace("\0", "a"));
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/users", HttpMethod.PUT, new HttpEntity<>(user), ShowUser.class)))
                .assertMsg("The surname is too long.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/users");
    }

    // Email
    @Test
    void testPutWithWrongEmail() {
        user.setEmail("hola");
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/users", HttpMethod.PUT, new HttpEntity<>(user), ShowUser.class)))
                .assertMsg("The email is invalid.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/users");
    }

    @Test
    void testPutWithNullOrEmptyEmail() {
        user.setEmail(null);
        ShowUser response = restTemplate.exchange(uri + "/users", HttpMethod.PUT, new HttpEntity<>(user), ShowUser.class).getBody();
        assertNotNull(response.getEmail(), "Email is null.");
        user.setEmail("");
        response = restTemplate.exchange(uri + "/users", HttpMethod.PUT, new HttpEntity<>(user), ShowUser.class).getBody();
        assertNotNull(response.getEmail(), "Email is empty.");
    }

    // Avatar
    @Test
    void testPutWithNullOrEmptyAvatar() {
        user.setAvatar(null);
        ShowUser response = restTemplate.exchange(uri + "/users", HttpMethod.PUT, new HttpEntity<>(user), ShowUser.class).getBody();
        assertNotNull(response.getAvatar(), "Avatar is not null.");
        user.setAvatar("");
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/users", HttpMethod.PUT, new HttpEntity<>(user), ShowUser.class)))
                .assertMsg("The avatar is invalid.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/users");
    }

    @Test
    void testPutWithIncorrectAvatar() {
        user.setAvatar("user");
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/users", HttpMethod.PUT, new HttpEntity<>(user), ShowUser.class)))
                .assertMsg("The avatar is invalid.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/users");
    }

    // Bio
    @Test
    void testPutWithNullOrEmptyBio() {
        user.setBio(null);
        ShowUser response = restTemplate.exchange(uri + "/users", HttpMethod.PUT, new HttpEntity<>(user), ShowUser.class).getBody();
        assertNotNull(response.getBio(), "Bio is null.");
        user.setBio("");
        response = restTemplate.exchange(uri + "/users", HttpMethod.PUT, new HttpEntity<>(user), ShowUser.class).getBody();
        assertEquals(showUser.getBio(), response.getBio(), "Bio is empty");
    }

    @Test
    void testPutWithBioGreaterTan500() {
        user.setBio(new String(new char[501]).replace("\0", "a"));
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/users", HttpMethod.PUT, new HttpEntity<>(user), ShowUser.class)))
                .assertMsg("The bio is too long.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/users");
    }

    // Location
    @Test
    void testPutWithNullOrEmptyLocation() {
        user.setLocation(null);
        ShowUser response = restTemplate.exchange(uri + "/users", HttpMethod.PUT, new HttpEntity<>(user), ShowUser.class).getBody();
        assertNotNull(response.getLocation(), "Location is null");
        user.setLocation("");
        response = restTemplate.exchange(uri + "/users", HttpMethod.PUT, new HttpEntity<>(user), ShowUser.class).getBody();
        assertEquals(showUser.getLocation(), response.getLocation(), "Location is not correct");
    }

    @Test
    void testPutWithLocationGreaterThan50() {
        user.setLocation(new String(new char[51]).replace("\0", "a"));
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/users", HttpMethod.PUT, new HttpEntity<>(user), ShowUser.class)))
                .assertMsg("The location is too long.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/users");
    }
}
