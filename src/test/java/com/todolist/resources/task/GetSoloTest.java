package com.todolist.resources.task;

import com.todolist.dtos.ShowTask;
import com.todolist.utilities.ManagerException;
import com.todolist.utilities.SQL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class GetSoloTest {

    @BeforeEach
    void setUp() {
        SQL.start("jdbc:mariadb://localhost:3306/todolist-api2", "root", "mazetosan$root");
        SQL.read("data/create.sql");
        SQL.read("data/populate.sql");
    }

    @Test
    void testGetSoloFine() {
        String uri = "http://localhost:8080/api/v1/tasks/1";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask response = restTemplate.getForObject(uri, ShowTask.class);
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
    }

    @Test
    void testGetSoloFields() {
        String uri = "http://localhost:8080/api/v1/tasks/1?fields=title,description,annotation,idTask";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask response = restTemplate.getForObject(uri, ShowTask.class);
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
        String uri = "http://localhost:8080/api/v1/tasks/1?fields=idTask,title,description,annotation,wrongField";
        RestTemplate restTemplate = new RestTemplate();
        ManagerException exception = new ManagerException(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri, ShowTask.class)));
        assertEquals("Bad Request", exception.getStatus(), "Status code is not correct");
        assertEquals("The fields are invalid.", exception.getMsg(), "Response body is not correct");
        assertEquals("/api/v1/tasks/1", exception.getPath(), "Path is not correct");
    }

    @Test
    void testGetSoloUpperFields() {
        String uri = "http://localhost:8080/api/v1/tasks/1?fields=IDTASK,TITLE,DESCRIPTION,ANNOTATION";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask response = restTemplate.getForObject(uri, ShowTask.class);
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
        String uri = "http://localhost:8080/api/v1/tasks/99";
        RestTemplate restTemplate = new RestTemplate();
        Throwable exception = assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri, ShowTask.class));
        assertEquals("404", exception.getMessage().split(":")[0].trim(), "Status code is not correct");
    }
}
