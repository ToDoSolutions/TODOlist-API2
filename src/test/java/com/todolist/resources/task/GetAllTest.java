package com.todolist.resources.task;

import com.todolist.model.ShowTask;
import com.todolist.utilities.ManagerException;
import com.todolist.utilities.SQL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class GetAllTest {

    @BeforeEach
    void setUp() {
        SQL.start("jdbc:mariadb://localhost:3306/todolist-api2", "root", "mazetosan$root");
        SQL.read("data/create.sql");
        SQL.read("data/populate.sql");
    }

    // Correcto
    @Test
    void testGetAllFine() {
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask[] response = restTemplate.getForObject(uri, ShowTask[].class);
        assertNotNull(response, "Response is null");
        assertEquals(7, response.length, "Length is not correct");
    }

    // Offset
    @Test
    void testGetAllWithCorrectOffset() {
        String uri = "http://localhost:8080/api/v1/tasks?offset=2";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask[] response = restTemplate.getForObject(uri, ShowTask[].class);
        assertNotNull(response, "Response is null");
        assertEquals(7, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithIncorrectOffset() {
        String uri = "http://localhost:8080/api/v1/tasks?offset=a";
        RestTemplate restTemplate = new RestTemplate();
        ManagerException exception = new ManagerException(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri, ShowTask[].class)));
        assertEquals("Bad Request", exception.getStatus(), "Status code is not correct");
        assertEquals("No information", exception.getMsg(), "Message is not correct");
        assertEquals("/api/v1/tasks", exception.getPath(), "Path is not correct");
    }

    @Test
    void testGetAllWithNegativeOffset() {
        String uri = "http://localhost:8080/api/v1/tasks?offset=-1";
        RestTemplate restTemplate = new RestTemplate();
        ManagerException exception = new ManagerException(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri, ShowTask[].class)));
        assertEquals("Bad Request", exception.getStatus(), "Status code is not correct");
        assertEquals("The offset must be positive.", exception.getMsg().trim(), "Message is not correct");
        assertEquals("/api/v1/tasks", exception.getPath(), "Path is not correct");
    }

    // Limit
    @Test
    void testGetAllWithCorrectLimit() {
        String uri = "http://localhost:8080/api/v1/tasks?limit=2";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask[] response = restTemplate.getForObject(uri, ShowTask[].class);
        assertNotNull(response, "Response is null");
        assertEquals(2, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithIncorrectLimit() {
        String uri = "http://localhost:8080/api/v1/tasks?limit=a";
        RestTemplate restTemplate = new RestTemplate();
        ManagerException exception = new ManagerException(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri, ShowTask[].class)));
        assertEquals("Bad Request", exception.getStatus(), "Status code is not correct");
        assertEquals("No information", exception.getMsg(), "Message is not correct");
        assertEquals("/api/v1/tasks", exception.getPath(), "Path is not correct");
    }

    @Test
    void testGetAllWithNegativeLimit() {
        String uri = "http://localhost:8080/api/v1/tasks?limit=-2";
        RestTemplate restTemplate = new RestTemplate();
        ManagerException exception = new ManagerException(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri, ShowTask[].class)));
        assertEquals("Bad Request", exception.getStatus(), "Status code is not correct");
        assertEquals("The limit must be positive", exception.getMsg().trim(), "Message is not correct");
        assertEquals("/api/v1/tasks", exception.getPath(), "Path is not correct");
    }

    // Offset + Limit
    @Test
    void testGetAllWithCorrectOffsetAndLimit() {
        String uri = "http://localhost:8080/api/v1/tasks?offset=2&limit=2";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask[] response = restTemplate.getForObject(uri, ShowTask[].class);
        assertNotNull(response, "Response is null");
        assertEquals(2, response.length, "Length is not correct");
    }

    // Order
    @Test
    void testGetAllWithCorrectOrder() {
        String uri = "http://localhost:8080/api/v1/tasks?order=title";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask[] response = restTemplate.getForObject(uri, ShowTask[].class);
        assertNotNull(response, "Response is null");
        assertEquals(7, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithIncorrectOrder() {
        String uri = "http://localhost:8080/api/v1/tasks?order=a";
        RestTemplate restTemplate = new RestTemplate();
        ManagerException exception = new ManagerException(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri, ShowTask[].class)));
        assertEquals("Bad Request", exception.getStatus(), "Status code is not correct");
        assertEquals("The order is invalid.", exception.getMsg(), "Message is not correct");
        assertEquals("/api/v1/tasks", exception.getPath(), "Path is not correct");
    }

    @Test
    void testGetAllWithOrderAndOffset() {
        String uri = "http://localhost:8080/api/v1/tasks?order=title&offset=2";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask[] response = restTemplate.getForObject(uri, ShowTask[].class);
        assertNotNull(response, "Response is null");
        assertEquals(7, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithOrderAndLimit() {
        String uri = "http://localhost:8080/api/v1/tasks?order=title&limit=2";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask[] response = restTemplate.getForObject(uri, ShowTask[].class);
        assertNotNull(response, "Response is null");
        assertEquals(2, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithOrderAndOffsetAndLimit() {
        String uri = "http://localhost:8080/api/v1/tasks?order=title&offset=2&limit=2";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask[] response = restTemplate.getForObject(uri, ShowTask[].class);
        assertNotNull(response, "Response is null");
        assertEquals(2, response.length, "Length is not correct");
    }

    // Fields
    @Test
    void testGetAllWithCorrectFields() {
        String uri = "http://localhost:8080/api/v1/tasks?fields=title,description";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask[] response = restTemplate.getForObject(uri, ShowTask[].class);
        assertNotNull(response, "Response is null");
        assertEquals(7, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithIncorrectFields() {
        String uri = "http://localhost:8080/api/v1/tasks?fields=a";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask[] response = restTemplate.getForObject(uri, ShowTask[].class);
        assertNotNull(response, "Response is null");
        assertEquals(7, response.length, "Length is not correct");
    }
}
