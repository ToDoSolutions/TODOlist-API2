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

@FlywayTest(additionalLocations = "db/testWithOutData", value = @DataSource(url = "jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
// @FlywayTest(additionalLocations = "db/testWithOutData", value = @DataSource(url = "jdbc:mysql://uqiweqtspt5rb4xp:uWHt8scUWIMHRDzt7HCg@b8iyr7xai8wk75ismpbt-mysql.services.clever-cloud.com:3306/b8iyr7xai8wk75ismpbt", username = "uqiweqtspt5rb4xp", password = "uWHt8scUWIMHRDzt7HCg"))
class PostTest {

    Group group;
    String uri = "http://localhost:8080/api/v1";
    // String uri = "https://todolist-api2.herokuapp.com/api/v1";
    RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        group = new Group();
        group.setName("Group 1");
        group.setDescription("Group 1 description");
        group.setIdGroup(0L);
        restTemplate = new RestTemplate();
    }

    // Correct
    @Test
    void testPostFine() {
        ShowGroup response = restTemplate.postForObject(uri + "/groups", group, ShowGroup.class);
        assertEquals(1, response.getIdGroup(), "IdGroup is not correct");
        response = restTemplate.getForObject(uri + "/groups/1", ShowGroup.class);
        assertEquals(1, response.getIdGroup(), "IdGroup is not correct");
    }

    // Name
    @Test
    void testPostWithNullOrEmptyName() {
        group.setName(null);
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/groups", group, ShowGroup.class));
        group.setName("");
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/groups", group, ShowGroup.class));
    }

    @Test
    void testPostWithNameGreaterThan50() {
        group.setName(new String(new char[51]).replace("\0", "a"));
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/groups", group, ShowGroup.class));
    }

    // Description
    @Test
    void testPostWithDescriptionGreaterThan500() {
        group.setDescription(new String(new char[501]).replace("\0", "a"));
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/groups", group, ShowGroup.class));
    }

    // CreatedDate
    @Test
    void testPostWithInvalidCreatedDate() {
        group.setCreatedDate("ayer");
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/groups", group, ShowGroup.class));
    }
}
