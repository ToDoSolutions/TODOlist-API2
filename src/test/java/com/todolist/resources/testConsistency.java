package com.todolist.resources;

import com.todolist.model.ShowTask;
import com.todolist.model.ShowUser;
import com.todolist.utilities.SQL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class testConsistency {

    @BeforeEach
    void setUp() {
        SQL.read("data/populate.sql");
    }

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
