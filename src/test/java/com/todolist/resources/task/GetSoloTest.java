package com.todolist.resources.task;

import com.todolist.model.ShowTask;
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
        String uri = "http://localhost:8080/api/v1/tasks/1";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask response = restTemplate.getForObject(uri, ShowTask.class);
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
    }

    @Test
    public void testGetSoloFields() {
        String uri = "http://localhost:8080/api/v1/tasks/1?fields=name,surname,email";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask response = restTemplate.getForObject(uri, ShowTask.class);
        assertEquals("Vacaciones", response.getTitle(), "Title is not correct");
        assertEquals("Quiero vacaciones", response.getDescription(), "Description is not correct");
        assertEquals("Vacaciones", response.getAnnotation(), "Annotation is not correct");
        assertEquals(null, response.getStatus(), "Status is not correct");
        assertEquals(null, response.getFinishedDate(), "FinishedDate is not correct");
        assertEquals(null, response.getStartDate(), "StartDate is not correct");
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
        assertEquals(null, response.getPriority(), "Priority is not correct");
        assertEquals(null, response.getDifficulty(), "Difficulty is not correct");
    }

    @Test
    public void testGetSoloFieldsWithWrongField() {
        String uri = "http://localhost:8080/api/v1/tasks/1?fields=idTask,title,description,annotation,wrongField";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri, ShowTask.class));
    }

    @Test
    public void testGetSoloUpperFields() {
        String uri = "http://localhost:8080/api/v1/tasks/1?fields=IDTASK,TITLE,DESCRIPTION,ANNOTATION";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask response = restTemplate.getForObject(uri, ShowTask.class);
        assertEquals("Vacaciones", response.getTitle(), "Title is not correct");
        assertEquals("Quiero vacaciones", response.getDescription(), "Description is not correct");
        assertEquals("Vacaciones", response.getAnnotation(), "Annotation is not correct");
        assertEquals(null, response.getStatus(), "Status is not correct");
        assertEquals(null, response.getFinishedDate(), "FinishedDate is not correct");
        assertEquals(null, response.getStartDate(), "StartDate is not correct");
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
        assertEquals(null, response.getPriority(), "Priority is not correct");
        assertEquals(null, response.getDifficulty(), "Difficulty is not correct");
    }


    @Test
    public void testGetSoloNotFound() {
        String uri = "http://localhost:8080/api/v1/tasks/99";
        RestTemplate restTemplate = new RestTemplate();
        Throwable exception = assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri, ShowTask.class));
        assertEquals("404", exception.getMessage().split(":")[0].trim(), "Status code is not correct");
    }
}
