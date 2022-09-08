package com.todolist.controllers.task;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.TODOlistApplication;
import com.todolist.dtos.ShowTask;
import com.todolist.exceptions.ManagerException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@FlywayTest(additionalLocations = "db/migration", value = @DataSource(url = "jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
// @FlywayTest(additionalLocations = "db/migration", value = @DataSource(url = "jdbc:mysql://uqiweqtspt5rb4xp:uWHt8scUWIMHRDzt7HCg@b8iyr7xai8wk75ismpbt-mysql.services.clever-cloud.com:3306/b8iyr7xai8wk75ismpbt", username = "uqiweqtspt5rb4xp", password = "uWHt8scUWIMHRDzt7HCg"))
class GetAllTest {

    String uri = "http://localhost:8080/api/v1/";
    // String uri = "https://todolist-api2.herokuapp.com/api/v1";
    RestTemplate restTemplate;

    @BeforeAll
    public static void beforeClass() {
        TODOlistApplication.main(new String[]{});
    }

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
    }

    // Correcto
    @Test
    void testGetAllFine() {
        ShowTask[] response = restTemplate.getForObject(uri + "/tasks", ShowTask[].class);
        assertNotNull(response, "Response is null");
        assertEquals(8, response.length, "Length is not correct");
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
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/tasks?offset=a", ShowTask[].class)))
                .assertMsg("Error while parsing the next string  \\a\\.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/tasks");
    }

    @Test
    void testGetAllWithNegativeOffset() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/tasks?offset=-1", ShowTask[].class)))
                .assertMsg("The offset must be positive.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/tasks");
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
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/tasks?limit=a", ShowTask[].class)))
                .assertMsg("Error while parsing the next string  \\a\\.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/tasks");
    }

    @Test
    void testGetAllWithNegativeLimit() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/tasks?limit=-2", ShowTask[].class)))
                .assertMsg("The limit must be positive.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/tasks");
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
        assertEquals(8, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithIncorrectOrder() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/tasks?order=a", ShowTask[].class)))
                .assertMsg("The order is invalid.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/tasks");
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
        assertEquals(8, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithIncorrectFields() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () ->restTemplate.getForObject(uri + "/tasks?fields=a", ShowTask[].class)))
                .assertMsg("The fields are invalid.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/tasks");
    }
}
