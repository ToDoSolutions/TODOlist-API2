package com.todolist.resources.user;

import com.todolist.model.ShowTask;
import com.todolist.model.ShowUser;
import com.todolist.utilities.SQL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GetSoloTest {

    @BeforeEach
    public void setUp() {
        SQL.start("jdbc:mariadb://localhost:3306/todolist-api2", "root", "mazetosan$root");
        SQL.read("data/populate.sql");
    }

    @Test
    public void testGetSoloFine() {
        String uri = "http://localhost:8080/api/v1/users/1";
        RestTemplate restTemplate = new RestTemplate();
        ShowUser response = restTemplate.getForObject(uri, ShowUser.class);
        assertEquals(1, response.getIdUser(), "IdUser is not correct");
    }

    @Test
    public void testGetSoloFields() {
        String uri = "http://localhost:8080/api/v1/users/1?fieldsUser=idUser,name,surname";
        RestTemplate restTemplate = new RestTemplate();
        ShowUser response = restTemplate.getForObject(uri, ShowUser.class);
        assertEquals(1, response.getIdUser(), "IdUser is not correct");
        assertEquals(null, response.getTasks(), "Tasks is not correct");
    }

    @Test
    public void testGetSoloFieldsWithWrongField() {
        String uri = "http://localhost:8080/api/v1/users/1?fieldsUser=idUser,wrongField";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri, ShowTask.class));
    }

    @Test
    public void testGetSoloUpperFields() {
        String uri = "http://localhost:8080/api/v1/users/1?fieldsUser=IDUSER,NAME,SURNAME";
        RestTemplate restTemplate = new RestTemplate();
        ShowUser response = restTemplate.getForObject(uri, ShowUser.class);
        assertEquals(1, response.getIdUser(), "IdUser is not correct");
        assertEquals(null, response.getTasks(), "Tasks is not correct");
    }


    @Test
    public void testGetSoloNotFound() {
        String uri = "http://localhost:8080/api/v1/users/99";
        RestTemplate restTemplate = new RestTemplate();
        Throwable exception = assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri, ShowUser.class));
        assertEquals("404", exception.getMessage().split(":")[0].trim(), "Status code is not correct");
    }
}
