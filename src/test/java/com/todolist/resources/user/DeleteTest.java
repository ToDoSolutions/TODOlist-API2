package com.todolist.resources.user;

import com.todolist.dtos.ShowUser;
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
        SQL.read("data/V2__populate_db.sql");
    }

    @Test
    void testDeleteFine() {
        String uri = "http://localhost:8080/api/v1/users/1";
        RestTemplate restTemplate = new RestTemplate();
        ShowUser response = restTemplate.exchange(uri, HttpMethod.DELETE, null, ShowUser.class).getBody();
        assertEquals(1, response.getIdUser(), "IdUser is not correct");
        Throwable exception = assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri, ShowUser.class));
        assertEquals("404", exception.getMessage().split(":")[0].trim(), "Status code is not correct");
    }

    @Test
    void testDeleteWithWrongId() {
        String uri = "http://localhost:8080/api/v1/groups/0";
        RestTemplate restTemplate = new RestTemplate();
        Throwable exception = assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.DELETE, null, ShowUser.class));
    }
}
