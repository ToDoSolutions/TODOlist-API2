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
        SQL sql = new SQL("jdbc:mariadb://localhost:3306/todolist-api2", "root", "iissi$root");
        // SQL sql = new SQL("jdbc:mysql://uqiweqtspt5rb4xp:uWHt8scUWIMHRDzt7HCg@b8iyr7xai8wk75ismpbt-mysql.services.clever-cloud.com:3306/b8iyr7xai8wk75ismpbt", "uqiweqtspt5rb4xp", "uWHt8scUWIMHRDzt7HCg");
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
}
