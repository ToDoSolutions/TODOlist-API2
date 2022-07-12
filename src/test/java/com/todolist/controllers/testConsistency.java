package com.todolist.controllers;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@FlywayTest(additionalLocations = "db/testWithData", value = @DataSource(url = "jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
class testConsistency {

    @Test
    void testDeleteTaskFromUser() {
        String uri1 = "http://localhost:8080/api/v1/tasks/1";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(uri1, HttpMethod.DELETE, null, ShowTask.class);
        String uri2 = "http://localhost:8080/api/v1/users/1";
        ShowUser response = restTemplate.getForObject(uri2, ShowUser.class);
        assertEquals(0, response.getTasks().size(), "Tasks are not deleted");
    }

    @Test
    void testDelteUserFromTask() {
        String uri1 = "http://localhost:8080/api/v1/users/1";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(uri1, HttpMethod.DELETE, null, ShowUser.class);
        String uri2 = "http://localhost:8080/api/v1/tasks/1";
        ShowTask response = restTemplate.getForObject(uri2, ShowTask.class);
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
    }
}
