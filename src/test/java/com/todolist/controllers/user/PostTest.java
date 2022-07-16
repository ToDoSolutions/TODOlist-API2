package com.todolist.controllers.user;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.dtos.ShowUser;
import com.todolist.entity.User;
import com.todolist.exceptions.ManagerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@FlywayTest(additionalLocations = "db/testWithOutData", value = @DataSource(url = "jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
// @FlywayTest(additionalLocations = "db/testWithOutData", value = @DataSource(url = "jdbc:mysql://uqiweqtspt5rb4xp:uWHt8scUWIMHRDzt7HCg@b8iyr7xai8wk75ismpbt-mysql.services.clever-cloud.com:3306/b8iyr7xai8wk75ismpbt", username = "uqiweqtspt5rb4xp", password = "uWHt8scUWIMHRDzt7HCg"))
class PostTest {

    User user;
    String uri = "http://localhost:8080/api/v1";
    // String uri = "https://todolist-api2.herokuapp.com/api/v1";
    RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        user = User.of("User 1", "User 1 surname", "User 1 username", "user@todolist.com", "https://avatar.com/user1", "User 1 bio", "User 1 location", "User 1 password");
        restTemplate = new RestTemplate();
    }

    // Correct
    @Test
    void testPostFine() {
        ShowUser response = restTemplate.postForObject(uri + "/users", user, ShowUser.class);
        assertEquals(1, response.getIdUser(), "IdUser is not correct");
        response = restTemplate.getForObject(uri + "/users/1", ShowUser.class);
        assertEquals(1, response.getIdUser(), "IdUser is not correct");
    }

    // Name
    @Test
    void testPostWithNullOrEmptyName() {
        user.setName(null);
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/users", user, ShowUser.class)))
                .assertMsg("The name cannot be null or empty.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/users");
        user.setName("");
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/users", user, ShowUser.class)))
                .assertMsg("The name cannot be null or empty.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/users");
    }

    @Test
    void testPostWithTitleGreaterThan50() {
        user.setName(new String(new char[51]).replace("\0", "a"));
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/users", user, ShowUser.class)))
                .assertMsg("The name cannot be greater than 50 characters.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/users");
    }

    // Surname
    @Test
    void testPostWithNullOrEmptySurname() {
        user.setSurname(null);
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/users", user, ShowUser.class)))
                .assertMsg("The surname cannot be null or empty.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/users");
        user.setSurname("");
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/users", user, ShowUser.class)))
                .assertMsg("The surname cannot be null or empty.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/users");
    }

    @Test
    void testPostWithSurnameGreaterThan50() {
        user.setSurname(new String(new char[51]).replace("\0", "a"));
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/users", user, ShowUser.class)))
                .assertMsg("The surname cannot be greater than 50 characters.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/users");
    }

    // Email
    @Test
    void testPostWithIncorrectEmail() {
        user.setEmail("user");
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/users", user, ShowUser.class)))
                .assertMsg("The email is not correct.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/users");
    }

    @Test
    void testPostWithNullEmail() {
        user.setEmail(null);
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/users", user, ShowUser.class)))
                .assertMsg("The email cannot be null.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/users");
        user.setEmail("hola");
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/users", user, ShowUser.class)))
                .assertMsg("The email is not correct.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/users");
    }

    // Avatar
    @Test
    void testPostWithNullOrEmptyAvatar() {
        user.setAvatar(null);
        ShowUser response = restTemplate.postForObject(uri + "/users", user, ShowUser.class);
        assertNull(response.getAvatar(), "Avatar is not null");
        user.setAvatar("");
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/users", user, ShowUser.class)))
                .assertMsg("The avatar cannot be null or empty.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/users");
    }

    @Test
    void testPostWithIncorrectAvatar() {
        user.setEmail("user");
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/users", user, ShowUser.class)))
                .assertMsg("The avatar is not correct.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/users");
    }

    // Bio
    @Test
    void testPostWithNullOrEmptyBio() {
        user.setBio(null);
        ShowUser response = restTemplate.postForObject(uri + "/users", user, ShowUser.class);
        assertNull(response.getBio(), "Bio is not null");
        user.setBio("");
        response = restTemplate.postForObject(uri + "/users", user, ShowUser.class);
        assertEquals("", response.getBio(), "Bio is not empty");
    }

    @Test
    void testPostWithBioGreaterTan500() {
        user.setBio(new String(new char[501]).replace("\0", "a"));
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/users", user, ShowUser.class)))
                .assertMsg("The bio cannot be greater than 500 characters.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/users");
    }

    // Location
    @Test
    void testPostWithNullOrEmptyLocation() {
        user.setLocation(null);
        ShowUser response = restTemplate.postForObject(uri + "/users", user, ShowUser.class);
        assertNull(response.getLocation(), "Location is not null");
        user.setLocation("");
        response = restTemplate.postForObject(uri + "/users", user, ShowUser.class);
        assertEquals("", response.getLocation(), "Location is not correct");
    }

    @Test
    void testPostWithLocationGreaterThan50() {
        user.setEmail(new String(new char[51]).replace("\0", "a"));
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/users", user, ShowUser.class)))
                .assertMsg("The location cannot be greater than 50 characters.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/users");
    }
}
