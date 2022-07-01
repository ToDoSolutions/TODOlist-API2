package com.todolist.resources.task;

import com.todolist.model.ShowTask;
import com.todolist.utilities.ManagerException;
import com.todolist.utilities.SQL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeleteTest {

    @BeforeEach
    void setUp() {
        SQL.start("jdbc:mariadb://localhost:3306/todolist-api2", "root", "mazetosan$root");
        SQL.read("data/create.sql");
        SQL.read("data/populate.sql");
    }

    @Test
    void testDeleteFine() {
        String uri = "http://localhost:8080/api/v1/tasks/1";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask response = restTemplate.exchange(uri, HttpMethod.DELETE, null, ShowTask.class).getBody();
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
        ManagerException exception = new ManagerException(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri, ShowTask.class)));
        assertEquals("Not Found", exception.getStatus(), "Status code is not correct");
        assertEquals("The task with idTask 1 does not exist.", exception.getMsg(), "Message is not correct");
        assertEquals("/api/v1/tasks/1", exception.getPath(), "Path is not correct");
    }

    @Test
    void testDeleteWithWrongId() {
        String uri = "http://localhost:8080/api/v1/tasks/0";
        RestTemplate restTemplate = new RestTemplate();
        ManagerException exception = new ManagerException(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.DELETE, null, ShowTask.class)));
        assertEquals("Not Found", exception.getStatus(), "Status code is not correct");
        assertEquals("The task with idTask 0 does not exist.", exception.getMsg(), "Message is not correct");
        assertEquals("/api/v1/tasks/0", exception.getPath(), "Path is not correct");
    }
}
