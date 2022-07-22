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
class GetSoloTest {

    String uri = "http://localhost:8080/api/v1";
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
        ShowTask response = restTemplate.getForObject(uri + "/tasks/1", ShowTask.class);
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
    }

    @Test
    void testGetSoloFields() {
        ShowTask response = restTemplate.getForObject(uri + "/tasks/1?fields=title,description,annotation,idTask", ShowTask.class);
        System.out.println(response);
        assertEquals("Vacaciones", response.getTitle(), "Title is not correct");
        assertEquals("Quiero vacaciones", response.getDescription(), "Description is not correct");
        assertEquals("Vacaciones", response.getAnnotation(), "Annotation is not correct");
        assertNull(response.getStatus(), "Status is not correct");
        assertNull(response.getFinishedDate(), "FinishedDate is not correct");
        assertNull(response.getStartDate(), "StartDate is not correct");
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
        assertNull(response.getPriority(), "Priority is not correct");
        assertNull(response.getDifficulty(), "Difficulty is not correct");
    }

    @Test
    void testGetSoloFieldsWithWrongField() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/tasks/1?fields=idTask,title,description,annotation,wrongField", ShowTask.class)))
                .assertMsg("The fields are invalid.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/tasks/1");
    }

    @Test
    void testGetSoloUpperFields() {
        ShowTask response = restTemplate.getForObject(uri + "/tasks/1?fields=IDTASK,TITLE,DESCRIPTION,ANNOTATION", ShowTask.class);
        assertEquals("Vacaciones", response.getTitle(), "Title is not correct");
        assertEquals("Quiero vacaciones", response.getDescription(), "Description is not correct");
        assertEquals("Vacaciones", response.getAnnotation(), "Annotation is not correct");
        assertNull(response.getStatus(), "Status is not correct");
        assertNull(response.getFinishedDate(), "FinishedDate is not correct");
        assertNull(response.getStartDate(), "StartDate is not correct");
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
        assertNull(response.getPriority(), "Priority is not correct");
        assertNull(response.getDifficulty(), "Difficulty is not correct");
    }

    @Test
    void testGetSoloNotFound() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/tasks/99", ShowTask.class)))
                .assertMsg("The task with idTask 99 does not exist.")
                .assertStatus("Not Found")
                .assertPath("/api/v1/tasks/99");
    }

    @Test
    void testGetSoloWithNegativeId() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/tasks/-1", ShowTask.class)))
                .assertMsg("The idTask must be positive.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/tasks/-1");
    }
}
