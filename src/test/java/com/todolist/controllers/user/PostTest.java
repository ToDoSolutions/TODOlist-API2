package com.todolist.controllers.user;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.dtos.ShowUser;
import com.todolist.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@FlywayTest(additionalLocations = "db/testWithOutData", value = @DataSource(url ="jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
class PostTest {

    User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("User 1");
        user.setSurname("User 1 surname");
        user.setEmail("user@todolist.com");
        user.setAvatar("https://avatar.com/user1");
        user.setBio("User 1 bio");
        user.setLocation("User 1 location");
        user.setIdUser(0L);
    }

    // Correct
    @Test
    void testPostFine() {
        String uri1 = "http://localhost:8080/api/v1/users";
        RestTemplate restTemplate = new RestTemplate();
        ShowUser response = restTemplate.postForObject(uri1, user, ShowUser.class);
        assertEquals(1, response.getIdUser(), "IdUser is not correct");
        String uri2 = "http://localhost:8080/api/v1/users/1";
        response = restTemplate.getForObject(uri2, ShowUser.class);
        assertEquals(1, response.getIdUser(), "IdUser is not correct");
    }

    // Name
    @Test
    void testPostWithNullOrEmptyName() {
        user.setName(null);
        String uri = "http://localhost:8080/api/v1/users";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, user, ShowUser.class));
        user.setName("");
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, user, ShowUser.class));
    }

    @Test
    void testPostWithTitleGreaterThan50() {
        user.setName(new String(new char[51]).replace("\0", "a"));
        String uri = "http://localhost:8080/api/v1/users";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, user, ShowUser.class));
    }

    // Surname
    @Test
    void testPostWithNullOrEmptySurname() {
        user.setSurname(null);
        String uri = "http://localhost:8080/api/v1/users";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, user, ShowUser.class));
        user.setSurname("");
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, user, ShowUser.class));
    }

    @Test
    void testPostWithSurnameGreaterThan50() {
        user.setSurname(new String(new char[51]).replace("\0", "a"));
        String uri = "http://localhost:8080/api/v1/users";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, user, ShowUser.class));
    }

    // Email
    @Test
    void testPostWithIncorrectEmail() {
        user.setEmail("user");
        String uri = "http://localhost:8080/api/v1/users";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, user, ShowUser.class));
    }

    @Test
    void testPostWithNullEmail() {
        user.setEmail(null);
        String uri = "http://localhost:8080/api/v1/users";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, user, ShowUser.class));
        user.setEmail("hola");
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, user, ShowUser.class));
    }

    // Avatar
    @Test
    void testPostWithNullOrEmptyAvatar() {
        user.setAvatar(null);
        String uri = "http://localhost:8080/api/v1/users";
        RestTemplate restTemplate = new RestTemplate();
        ShowUser response = restTemplate.postForObject(uri, user, ShowUser.class);
        assertNull(response.getAvatar(), "Avatar is not null");
        user.setAvatar("");
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, user, ShowUser.class));
    }

    @Test
    void testPostWithIncorrectAvatar() {
        user.setEmail("user");
        String uri = "http://localhost:8080/api/v1/users";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, user, ShowUser.class));
    }

    // Bio
    @Test
    void testPostWithNullOrEmptyBio() {
        user.setBio(null);
        String uri = "http://localhost:8080/api/v1/users";
        RestTemplate restTemplate = new RestTemplate();
        ShowUser response = restTemplate.postForObject(uri, user, ShowUser.class);
        assertNull(response.getBio(), "Bio is not null");
        user.setBio("");
        response = restTemplate.postForObject(uri, user, ShowUser.class);
        assertEquals("", response.getBio(), "Bio is not empty");
    }

    @Test
    void testPostWithBioGreaterTan500() {
        user.setBio(new String(new char[501]).replace("\0", "a"));
        String uri = "http://localhost:8080/api/v1/users";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, user, ShowUser.class));
    }

    // Location
    @Test
    void testPostWithNullOrEmptyLocation() {
        user.setLocation(null);
        String uri = "http://localhost:8080/api/v1/users";
        RestTemplate restTemplate = new RestTemplate();
        ShowUser response = restTemplate.postForObject(uri, user, ShowUser.class);
        assertNull(response.getLocation(), "Location is not null");
        user.setLocation("");
        response = restTemplate.postForObject(uri, user, ShowUser.class);
        assertEquals("", response.getLocation(), "Location is not correct");
    }

    @Test
    void testPostWithLocationGreaterThan50() {
        user.setEmail(new String(new char[51]).replace("\0", "a"));
        String uri = "http://localhost:8080/api/v1/users";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, user, ShowUser.class));
    }
}
