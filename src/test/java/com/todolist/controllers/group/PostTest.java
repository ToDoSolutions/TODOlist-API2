package com.todolist.controllers.group;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.dtos.ShowGroup;
import com.todolist.entity.Group;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@FlywayTest(additionalLocations = "db/testWithOutData", value = @DataSource(url ="jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
public class PostTest {

    Group group;

    @BeforeEach
    void setUp() {
        group = new Group();
        group.setName("Group 1");
        group.setDescription("Group 1 description");
        group.setIdGroup(0);
    }

    // Correct
    @Test
    void testPostFine() {
        String uri1 = "http://localhost:8080/api/v1/groups";
        RestTemplate restTemplate = new RestTemplate();
        ShowGroup response = restTemplate.postForObject(uri1, group, ShowGroup.class);
        assertEquals(1, response.getIdGroup(), "IdGroup is not correct");
        String uri2 = "http://localhost:8080/api/v1/groups/1";
        response = restTemplate.getForObject(uri2, ShowGroup.class);
        assertEquals(1, response.getIdGroup(), "IdGroup is not correct");
    }

    // Name
    @Test
    void testPostWithNullOrEmptyName() {
        group.setName(null);
        String uri = "http://localhost:8080/api/v1/groups";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, group, ShowGroup.class));
        group.setName("");
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, group, ShowGroup.class));
    }

    @Test
    void testPostWithNameGreaterThan50() {
        group.setName(new String(new char[51]).replace("\0", "a"));
        String uri = "http://localhost:8080/api/v1/groups";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, group, ShowGroup.class));
    }

    // Description
    @Test
    void testPostWithNullOrEmptyDescription() {
        group.setDescription(null);
        String uri = "http://localhost:8080/api/v1/groups";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, group, ShowGroup.class));
        group.setDescription("");
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, group, ShowGroup.class));
    }

    @Test
    void testPostWithDescriptionGreaterThan500() {
        group.setDescription(new String(new char[501]).replace("\0", "a"));
        String uri = "http://localhost:8080/api/v1/groups";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, group, ShowGroup.class));
    }

    // CreatedDate
    @Test
    void testPostWithInvalidCreatedDate() {
        group.setCreatedDate("ayer");
        String uri = "http://localhost:8080/api/v1/groups";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, group, ShowGroup.class));
    }
}
