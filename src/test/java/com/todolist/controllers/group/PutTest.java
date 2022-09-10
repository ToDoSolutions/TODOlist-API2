package com.todolist.controllers.group;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.TODOlistApplication;
import com.todolist.dtos.ShowGroup;
import com.todolist.entity.Group;
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

    Group group;
    ShowGroup showGroup;
    String uri = "http://localhost:8080/api/v1";
    // String uri = "https://todolist-api2.herokuapp.com/api/v1";
    RestTemplate restTemplate;

    @BeforeAll
    public static void beforeClass() {
        TODOlistApplication.main(new String[]{});
    }

    @BeforeEach
    void setUp() {
        group = new Group();
        group.setName("Group 1");
        group.setDescription("Group 1 description");
        restTemplate = new RestTemplate();
        showGroup = restTemplate.postForObject(uri + "/group", group, ShowGroup.class);
        System.out.println("testPutFine");
        group.setIdGroup(showGroup.getIdGroup());
    }

    // Correct
    @Test
    void testPutFine() {
        group.setName("Group 2");
        group.setDescription("Group 2 description");
        group.setIdGroup(showGroup.getIdGroup());
        ShowGroup response = restTemplate.exchange(uri + "/group", HttpMethod.PUT, new HttpEntity<>(group), ShowGroup.class).getBody();
        assertEquals(1, response.getIdGroup(), "IdGroup is not correct");
        response = restTemplate.getForObject(uri + "/group/1", ShowGroup.class);
        assertEquals(1, response.getIdGroup(), "IdGroup is not correct");
    }

    // Name
    @Test
    void testPutWithNullOrEmptyName() {
        group.setName(null);
        ShowGroup response = restTemplate.exchange(uri + "/group", HttpMethod.PUT, new HttpEntity<>(group), ShowGroup.class).getBody();
        assertNotNull(response.getName(), "Name is null.");
        group.setName("");
        response = restTemplate.exchange(uri + "/group", HttpMethod.PUT, new HttpEntity<>(group), ShowGroup.class).getBody();
        assertEquals(showGroup.getName(), response.getName(), "Name is not correct.");
    }

    @Test
    void testPutWithNameGreaterThan50() {
        group.setName(new String(new char[51]).replace("\0", "a"));
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/group", HttpMethod.PUT, new HttpEntity<>(group), ShowGroup.class)))
                .assertMsg("The name is too long.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/group");
    }

    // Description
    @Test
    void testPutWithNullOrEmptyDescription() {
        group.setDescription(null);
        ShowGroup response = restTemplate.exchange(uri + "/group", HttpMethod.PUT, new HttpEntity<>(group), ShowGroup.class).getBody();
        assertNotNull(response.getDescription(), "Description is null.");
        group.setDescription("");
        response = restTemplate.exchange(uri + "/group", HttpMethod.PUT, new HttpEntity<>(group), ShowGroup.class).getBody();
        assertEquals(showGroup.getDescription(), response.getDescription(), "Description is not correct.");
    }

    @Test
    void testPutWithDescriptionGreaterThan500() {
        group.setDescription(new String(new char[501]).replace("\0", "a"));
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/group", HttpMethod.PUT, new HttpEntity<>(group), ShowGroup.class)))
                .assertMsg("The description is too long.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/group");
    }

    // CreatedDate
    @Test
    void testPutWithInvalidCreatedDate() {
        group.setCreatedDate("ayer");
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/group", HttpMethod.PUT, new HttpEntity<>(group), ShowGroup.class)))
                .assertMsg("The createdDate is invalid.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/group");
    }

    @Test
    void testPutWithNullOrEmptyCreatedDate() {
        group.setCreatedDate(null);
        ShowGroup response = restTemplate.exchange(uri + "/group", HttpMethod.PUT, new HttpEntity<>(group), ShowGroup.class).getBody();
        assertNotNull(response.getCreatedDate(), "CreatedDate is null.");
        group.setCreatedDate("");
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/group", HttpMethod.PUT, new HttpEntity<>(group), ShowGroup.class)))
                .assertMsg("The createdDate is invalid.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/group");
        assertEquals(showGroup.getCreatedDate(), response.getCreatedDate(), "CreatedDate is not correct.");
    }
}
