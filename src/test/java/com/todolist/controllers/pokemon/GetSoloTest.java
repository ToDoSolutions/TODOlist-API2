package com.todolist.controllers.pokemon;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.TODOlistApplication;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.Status;
import com.todolist.exceptions.ManagerException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@FlywayTest(additionalLocations = "db/migration", value = @DataSource(url = "jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
// @FlywayTest(additionalLocations = "db/migration", value = @DataSource(url = "jdbc:mysql://uqiweqtspt5rb4xp:uWHt8scUWIMHRDzt7HCg@b8iyr7xai8wk75ismpbt-mysql.services.clever-cloud.com:3306/b8iyr7xai8wk75ismpbt", username = "uqiweqtspt5rb4xp", password = "uWHt8scUWIMHRDzt7HCg"))
public class GetSoloTest {

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

    @Test
    void testGetSoloFine() {
        ShowTask response = restTemplate.getForObject(uri + "/pokemon/pikachu?days=1", ShowTask.class);
        assertEquals("Catch: pikachu", response.getTitle(), "The name of the task is incorrect.");
    }

    // Status
    @Test
    void testGetSoloSetStatus() {
        ShowTask response = restTemplate.getForObject(uri + "/pokemon/pikachu?days=1&status=done", ShowTask.class);
        assertEquals(Status.DONE, response.getStatus(), "The status of the task is incorrect.");
    }

    @Test
    void testGetSoloSetIncorrectStatus() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/pokemon/pikachu?days=1&status=pipo", ShowTask.class)))
                .assertMsg("The status pipo is not valid and it should be one of the following -> draft - in_progress - in_revision - done - cancelled.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/pokemon/pikachu");
    }

    // Finished date
    @Test
    void testGetSoloSetFinishedDate() {
        ShowTask response = restTemplate.getForObject(uri + "/pokemon/pikachu?finishedDate=2021-01-01", ShowTask.class);
        assertEquals(LocalDate.of(2021, 1, 1), response.getFinishedDate(), "The finished date of the task is incorrect.");
    }

    @Test
    void testGetSoloSetIncorrectFinishedDate() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/pokemon/pikachu?finishedDate=pipo", ShowTask.class)))
                .assertMsg("The finishedDate must be in format yyyy-MM-dd.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/pokemon/pikachu");
    }

    // Start date
    @Test
    void testGetSoloSetStartDate() {
        ShowTask response = restTemplate.getForObject(uri + "/pokemon/pikachu?startDate=2021-01-01&days=1", ShowTask.class);
        assertEquals(LocalDate.of(2021, 1, 1), response.getStartDate(), "The start date of the task is incorrect.");
    }

    @Test
    void testGetSoloSetIncorrectStartDate() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/pokemon/pikachu?startDate=pipo&days=1", ShowTask.class)))
                .assertMsg("The date pipo is not valid and it should be in the format yyyy-MM-dd.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/pokemon/pikachu");
    }

    // Priority
    @Test
    void testGetSoloSetPriority() {
        ShowTask response = restTemplate.getForObject(uri + "/pokemon/pikachu?priority=1&days=1", ShowTask.class);
        assertEquals(1, response.getPriority(), "The priority of the task is incorrect.");
    }

    @Test
    void testGetSoloSetIncorrectPriority() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/pokemon/pikachu?priority=pipo", ShowTask.class)))
                .assertMsg("Error while parsing the next string  \\pipo\\.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/pokemon/pikachu");
    }

    @Test
    void testGetSoloSetNegativePriority() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/pokemon/pikachu?priority=-1&days=1", ShowTask.class)))
                .assertMsg(" The priority must be between 0 and 5.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/pokemon/pikachu");
    }
}
