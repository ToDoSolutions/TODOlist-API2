package com.todolist.controllers.task;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.config.errors.ManagerException;
import com.todolist.dtos.ShowTask;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

// @FlywayTest(additionalLocations = "db/testWithData", value = @DataSource(url = "jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
@FlywayTest(additionalLocations = "db/testWithData", value = @DataSource(url = "jdbc:mysql://uqiweqtspt5rb4xp:uWHt8scUWIMHRDzt7HCg@b8iyr7xai8wk75ismpbt-mysql.services.clever-cloud.com:3306/b8iyr7xai8wk75ismpbt", username = "uqiweqtspt5rb4xp", password = "uWHt8scUWIMHRDzt7HCg"))
class GetSoloTest {

    // String uri = "http://localhost:8080/api/v1";
    String uri = "https://todolist-api2.herokuapp.com/api/v1";
    RestTemplate restTemplate;

    @Test
    void testGetSoloFine() {
        ShowTask response = restTemplate.getForObject(uri + "/tasks/1", ShowTask.class);
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
    }

    @Test
    void testGetSoloFields() {
        ShowTask response = restTemplate.getForObject(uri + "/tasks/1?fields=title,description,annotation,idTask", ShowTask.class);
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
        ManagerException exception = new ManagerException(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/tasks/1?fields=idTask,title,description,annotation,wrongField", ShowTask.class)));
        assertEquals("Bad Request", exception.getStatus(), "Status code is not correct");
        assertEquals("The fields are invalid.", exception.getMsg(), "Response body is not correct");
        assertEquals("/api/v1/tasks/1", exception.getPath(), "Path is not correct");
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
        Throwable exception = assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/tasks/99", ShowTask.class));
        assertEquals("404", exception.getMessage().split(":")[0].trim(), "Status code is not correct");
    }
}
