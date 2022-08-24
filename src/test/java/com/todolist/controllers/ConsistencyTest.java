package com.todolist.controllers;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.TODOlistApplication;
import com.todolist.dtos.ShowGroup;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@FlywayTest(additionalLocations = "db/migration", value = @DataSource(url = "jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
// @FlywayTest(additionalLocations = "db/migration", value = @DataSource(url = "jdbc:mysql://uqiweqtspt5rb4xp:uWHt8scUWIMHRDzt7HCg@b8iyr7xai8wk75ismpbt-mysql.services.clever-cloud.com:3306/b8iyr7xai8wk75ismpbt", username = "uqiweqtspt5rb4xp", password = "uWHt8scUWIMHRDzt7HCg"))
class ConsistencyTest {

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
    void testDeleteTaskFromUser() {
        restTemplate.exchange(uri + "/tasks/1", HttpMethod.DELETE, null, ShowTask.class);
        ShowUser response = restTemplate.getForObject(uri + "/users/1", ShowUser.class);
        assertEquals(0, response.getTasks().size(), "Task was not deleted");
    }

    @Test
    void testDelteUserFromTask() {
        restTemplate.exchange(uri + "/users/1", HttpMethod.DELETE, null, ShowUser.class);
        ShowTask response = restTemplate.getForObject(uri + "/tasks/1", ShowTask.class);
        assertEquals(1, response.getIdTask(), "Task was deleted.");
    }

    @Test
    void testDeleteUserFromGroup() {
        restTemplate.exchange(uri + "/users/1", HttpMethod.DELETE, null, ShowUser.class);
        ShowGroup response = restTemplate.getForObject(uri + "/groups/1", ShowGroup.class);
        assertEquals(1, response.getUsers().size(), "User was not deleted.");
    }

    @Test
    void testDeleteGroupFromUser() {
        restTemplate.exchange(uri + "/groups/1", HttpMethod.DELETE, null, ShowGroup.class);
        ShowUser response = restTemplate.getForObject(uri + "/users/1", ShowUser.class);
        assertEquals(1, response.getIdUser(), "User was deleted.");
    }

}
