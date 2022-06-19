package com.todolist.resources.task;

import com.todolist.model.ShowTask;
import com.todolist.model.ShowUser;
import com.todolist.utilities.SQL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeleteTest {

    @BeforeEach
    public void setUp() {
        SQL sql = new SQL("jdbc:mariadb://34.175.10.202:3306/todolist", "root", "todolist$root");
        sql.read("data/populate.sql");
    }

    @Test
    public void testDeleteFine() {
        String uri = "http://localhost:8080/api/v1/tasks/1";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask response = restTemplate.exchange(uri, HttpMethod.DELETE, null, ShowTask.class).getBody();
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
        Throwable exception = assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri, ShowTask.class));
        assertEquals("404", exception.getMessage().split(":")[0].trim(), "Status code is not correct");
    }

    @Test
    public void testDeleteWithWrongId() {
        String uri = "http://localhost:8080/api/v1/tasks/0";
        RestTemplate restTemplate = new RestTemplate();
        Throwable exception = assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.DELETE, null, ShowTask.class));
    }

    // Consistency check:
    @Test
    public void testDeleteConsistencyWithUser() {
        String uri1 = "http://localhost:8080/api/v1/tasks/1";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(uri1, HttpMethod.DELETE, null, ShowTask.class);
        String uri2 = "http://localhost:8080/api/v1/users/1";
        ShowUser response = restTemplate.getForObject(uri2, ShowUser.class);
        assertEquals(0, response.getTasks().size(), "Tasks are not deleted");
    }

    @Test
    void testDeleteConsistencyWithUserInverse() {
        String uri1 = "http://localhost:8080/api/v1/users/1";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(uri1, HttpMethod.DELETE, null, ShowUser.class);
        String uri2 = "http://localhost:8080/api/v1/tasks/1";
        ShowTask response = restTemplate.getForObject(uri2, ShowTask.class);
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
    }
}
