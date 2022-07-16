package com.todolist.controllers.group;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.dtos.ShowGroup;
import com.todolist.entity.Group;
import com.todolist.exceptions.ManagerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// @FlywayTest(additionalLocations = "db/testWithOutData", value = @DataSource(url = "jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
@FlywayTest(additionalLocations = "db/testWithOutData", value = @DataSource(url = "jdbc:mysql://uqiweqtspt5rb4xp:uWHt8scUWIMHRDzt7HCg@b8iyr7xai8wk75ismpbt-mysql.services.clever-cloud.com:3306/b8iyr7xai8wk75ismpbt", username = "uqiweqtspt5rb4xp", password = "uWHt8scUWIMHRDzt7HCg"))
class PutTest {

    Group group;
    ShowGroup showGroup;
    // String uri = "http://localhost:8080/api/v1";
    String uri = "https://todolist-api2.herokuapp.com/api/v1";
    RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        group = new Group();
        group.setName("Group 1");
        group.setDescription("Group 1 description");
        String uri = "http://localhost:8080/api/v1/groups";
        restTemplate = new RestTemplate();
        showGroup = restTemplate.postForObject(uri, group, ShowGroup.class);
    }

    // Correct
    @Test
    void testPostFine() {
        group.setName("Group 2");
        group.setDescription("Group 2 description");
        group.setIdGroup(showGroup.getIdGroup());
        ShowGroup response = restTemplate.exchange(uri + "/groups", HttpMethod.PUT, new HttpEntity<>(group), ShowGroup.class).getBody();
        assertEquals(1, response.getIdGroup(), "IdGroup is not correct");
        response = restTemplate.getForObject(uri + "/groups/1", ShowGroup.class);
        assertEquals(1, response.getIdGroup(), "IdGroup is not correct");
    }

    // Name
    @Test
    void testPostWithNullOrEmptyName() {
        group.setName(null);
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/groups", HttpMethod.PUT, new HttpEntity<>(group), ShowGroup.class)))
                .assertMsg("Name is required")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/groups");
        group.setName("");
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/groups", HttpMethod.PUT, new HttpEntity<>(group), ShowGroup.class)))
                .assertMsg("Name is required")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/groups");
    }

    @Test
    void testPostWithNameGreaterThan50() {
        group.setName(new String(new char[51]).replace("\0", "a"));
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/groups", HttpMethod.PUT, new HttpEntity<>(group), ShowGroup.class)))
                .assertMsg("Name must be less than 50 characters")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/groups");
    }

    // Description
    @Test
    void testPostWithNullOrEmptyDescription() {
        group.setDescription(null);
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/groups", HttpMethod.PUT, new HttpEntity<>(group), ShowGroup.class)))
                .assertMsg("Description is required")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/groups");
        group.setDescription("");
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/groups", HttpMethod.PUT, new HttpEntity<>(group), ShowGroup.class)))
                .assertMsg("Description is required")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/groups");
    }

    @Test
    void testPostWithDescriptionGreaterThan500() {
        group.setDescription(new String(new char[501]).replace("\0", "a"));
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/groups", HttpMethod.PUT, new HttpEntity<>(group), ShowGroup.class)))
                .assertMsg("Description must be less than 500 characters")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/groups");
    }

    // CreatedDate
    @Test
    void testPostWithInvalidCreatedDate() {
        group.setCreatedDate("ayer");
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/groups", HttpMethod.PUT, new HttpEntity<>(group), ShowGroup.class)))
                .assertMsg("CreatedDate is not a valid date")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/groups");
    }
}
