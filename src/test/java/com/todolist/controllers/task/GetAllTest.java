package com.todolist.controllers.task;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.config.errors.ManagerException;
import com.todolist.dtos.ShowTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

// @FlywayTest(additionalLocations = "db/testWithData", value = @DataSource(url = "jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
@FlywayTest(additionalLocations = "db/testWithData", value = @DataSource(url = "jdbc:mysql://uqiweqtspt5rb4xp:uWHt8scUWIMHRDzt7HCg@b8iyr7xai8wk75ismpbt-mysql.services.clever-cloud.com:3306/b8iyr7xai8wk75ismpbt", username = "uqiweqtspt5rb4xp", password = "uWHt8scUWIMHRDzt7HCg"))
class GetAllTest {

    // String uri = "http://localhost:8080/api/v1/";
    String uri = "https://todolist-api2.herokuapp.com/api/v1";
    RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
    }

    // Correcto
    @Test
    void testGetAllFine() {
        ShowTask[] response = restTemplate.getForObject(uri + "/tasks", ShowTask[].class);
        assertNotNull(response, "Response is null");
        assertEquals(7, response.length, "Length is not correct");
    }

    // Offset
    @Test
    void testGetAllWithCorrectOffset() {
        ShowTask[] response = restTemplate.getForObject(uri + "/tasks?offset=2", ShowTask[].class);
        assertNotNull(response, "Response is null");
        assertEquals(7, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithIncorrectOffset() {
        ManagerException exception = new ManagerException(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/tasks?offset=a", ShowTask[].class)));
        assertEquals("Bad Request", exception.getStatus(), "Status code is not correct");
        assertEquals("No information", exception.getMsg(), "Message is not correct");
        assertEquals("/api/v1/tasks", exception.getPath(), "Path is not correct");
    }

    @Test
    void testGetAllWithNegativeOffset() {
        ManagerException exception = new ManagerException(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/tasks?offset=-1", ShowTask[].class)));
        assertEquals("Bad Request", exception.getStatus(), "Status code is not correct");
        assertEquals("The offset must be positive.", exception.getMsg().trim(), "Message is not correct");
        assertEquals("/api/v1/tasks", exception.getPath(), "Path is not correct");
    }

    // Limit
    @Test
    void testGetAllWithCorrectLimit() {
        ShowTask[] response = restTemplate.getForObject(uri + "/tasks?limit=2", ShowTask[].class);
        assertNotNull(response, "Response is null");
        assertEquals(2, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithIncorrectLimit() {
        ManagerException exception = new ManagerException(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/tasks?limit=a", ShowTask[].class)));
        assertEquals("Bad Request", exception.getStatus(), "Status code is not correct");
        assertEquals("No information", exception.getMsg(), "Message is not correct");
        assertEquals("/api/v1/tasks", exception.getPath(), "Path is not correct");
    }

    @Test
    void testGetAllWithNegativeLimit() {
        ManagerException exception = new ManagerException(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/tasks?limit=-2", ShowTask[].class)));
        assertEquals("Bad Request", exception.getStatus(), "Status code is not correct");
        assertEquals("The limit must be positive", exception.getMsg().trim(), "Message is not correct");
        assertEquals("/api/v1/tasks", exception.getPath(), "Path is not correct");
    }

    // Offset + Limit
    @Test
    void testGetAllWithCorrectOffsetAndLimit() {
        ShowTask[] response = restTemplate.getForObject(uri + "/tasks?offset=2&limit=2", ShowTask[].class);
        assertNotNull(response, "Response is null");
        assertEquals(2, response.length, "Length is not correct");
    }

    // Order
    @Test
    void testGetAllWithCorrectOrder() {
        ShowTask[] response = restTemplate.getForObject(uri + "/tasks?order=title", ShowTask[].class);
        assertNotNull(response, "Response is null");
        assertEquals(7, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithIncorrectOrder() {
        ManagerException exception = new ManagerException(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/tasks?order=a", ShowTask[].class)));
        assertEquals("Bad Request", exception.getStatus(), "Status code is not correct");
        assertEquals("The order is invalid.", exception.getMsg(), "Message is not correct");
        assertEquals("/api/v1/tasks", exception.getPath(), "Path is not correct");
    }

    @Test
    void testGetAllWithOrderAndOffset() {
        ShowTask[] response = restTemplate.getForObject(uri + "/tasks?order=title&offset=2", ShowTask[].class);
        assertNotNull(response, "Response is null");
        assertEquals(7, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithOrderAndLimit() {
        ShowTask[] response = restTemplate.getForObject(uri + "/tasks?order=title&limit=2", ShowTask[].class);
        assertNotNull(response, "Response is null");
        assertEquals(2, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithOrderAndOffsetAndLimit() {
        ShowTask[] response = restTemplate.getForObject(uri + "/tasks?order=title&offset=2&limit=2", ShowTask[].class);
        assertNotNull(response, "Response is null");
        assertEquals(2, response.length, "Length is not correct");
    }

    // Fields
    @Test
    void testGetAllWithCorrectFields() {
        ShowTask[] response = restTemplate.getForObject(uri + "/tasks?fields=title,description", ShowTask[].class);
        assertNotNull(response, "Response is null");
        assertEquals(7, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithIncorrectFields() {
        ShowTask[] response = restTemplate.getForObject(uri + "/tasks?fields=a", ShowTask[].class);
        assertNotNull(response, "Response is null");
        assertEquals(7, response.length, "Length is not correct");
    }
}
