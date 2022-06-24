package com.todolist.resources.group;

import com.todolist.model.ShowGroup;
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
        SQL.start("jdbc:mariadb://localhost:3306/todolist-api2", "root", "mazetosan$root");
        SQL.read("data/populate.sql");
    }

    @Test
    public void testDeleteFine() {
        String uri = "http://localhost:8080/api/v1/groups/1";
        RestTemplate restTemplate = new RestTemplate();
        ShowGroup response = restTemplate.exchange(uri, HttpMethod.DELETE, null, ShowGroup.class).getBody();
        assertEquals(1, response.getIdGroup(), "IdGroup is not correct");
        Throwable exception = assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri, ShowGroup.class));
        assertEquals("404", exception.getMessage().split(":")[0].trim(), "Status code is not correct");
    }

    @Test
    public void testDeleteWithWrongId() {
        String uri = "http://localhost:8080/api/v1/groups/0";
        RestTemplate restTemplate = new RestTemplate();
        Throwable exception = assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.DELETE, null, ShowGroup.class));
    }
}
