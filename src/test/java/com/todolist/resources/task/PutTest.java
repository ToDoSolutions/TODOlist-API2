package com.todolist.resources.task;

import com.todolist.entity.Task;
import com.todolist.model.Difficulty;
import com.todolist.model.ShowTask;
import com.todolist.model.Status;
import com.todolist.utilities.SQL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PutTest {

    Task task;
    ShowTask showTask;

    @BeforeEach
    public void setUp() {
        SQL.start("jdbc:mariadb://localhost:3306/todolist-api2", "root", "mazetosan$root");
        SQL.read("data/create.sql");
        task = new Task();
        task.setTitle("Task 1");
        task.setDescription("Task 1 description");
        task.setAnnotation("Task 1 annotation");
        task.setStatus("DRAFT");
        task.setFinishedDate("3000-01-22");
        task.setStartDate("2015-01-01");
        task.setDifficulty("EASY");
        task.setPriority(1);
        task.setIdTask(1);
        String uri1 = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        showTask = restTemplate.postForObject(uri1, task, ShowTask.class);
    }

    // Correct
    @Test
    public void testPutFinet() {
        String uri1 = "http://localhost:8080/api/v1/tasks/";
        RestTemplate restTemplate = new RestTemplate();
        task.setTitle("Task 2");
        task.setDescription("Task 2 description");
        task.setAnnotation("Task 2 annotation");
        task.setStatus("DONE");
        task.setFinishedDate("3000-01-22");
        task.setStartDate("2015-01-01");
        task.setDifficulty("EASY");
        task.setPriority(1);
        task.setIdTask(showTask.getIdTask());
        ShowTask response = restTemplate.exchange(uri1, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
        String uri2 = "http://localhost:8080/api/v1/tasks/1";
        response = restTemplate.getForObject(uri2, ShowTask.class);
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
    }

    // Not exist
    @Test
    public void testOutNotExists()  {
        String uri = "http://localhost:8080/api/v1/tasks/";
        RestTemplate restTemplate = new RestTemplate();
        task.setIdTask(99L);
        task.setTitle("Task 2");
        task.setDescription("Task 2 description");
        task.setAnnotation("Task 2 annotation");
        task.setStatus("DONE");
        task.setFinishedDate("3000-01-22");
        task.setStartDate("2015-01-01");
        task.setDifficulty("EASY");
        task.setPriority(1);
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class));
    }

    // Title
    @Test
    public void testPostWithNullOrEmptyTitle() {
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        task.setTitle("");
        assertThrows(HttpClientErrorException.class, () ->  restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class));
    }

    @Test
    public void testPostWithTitleGreaterThan50() {
        task.setTitle("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class));
    }

    // Description
    @Test
    public void testPostWithEmptyDescription() {
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        task.setDescription("");
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class));
    }

    @Test
    public void testPostWithDescriptionGreaterThan200() {
        task.setDescription("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class));
    }

    // Annotation
    @Test
    public void testPostWithAnnotationGreaterThan50() {
        task.setAnnotation("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class));
    }

    // Status
    @Test
    public void testPostWithWrongStatus() {
        task.setStatus("WRONG");
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class));
    }

    @Test
    public void testPostWithLowerStatus() {
        task.setStatus("in progress");
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask response = restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
        assertEquals(Status.IN_PROGRESS, response.getStatus(), "Status is not correct");
    }

    // Relation between StartDate and FinishedDate
    @Test
    public void testPostWithStartDateIsAfterFinishedDate() {
        task.setStartDate("3001-01-30");
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class));
    }

    // StartDate
    @Test
    public void testPostWithWrongPatternInStartDate() {
        task.setStartDate("2015/01/30");
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class));
    }

    @Test
    public void testPostWithNullStartDate() {
        task.setStartDate(null);
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask response = restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
        assertEquals(showTask.getStartDate(), response.getStartDate(), "StartDate is not correct");
    }

    // FinishedDate
    @Test
    public void testPostWithWrongPatternInFinishedDate() {
        task.setFinishedDate("2015/01/30");
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class));
    }

    @Test
    public void testPostWithNullFinishedDate() {
        task.setFinishedDate(null);
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask response = restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertEquals(showTask.getFinishedDate(), response.getFinishedDate(), "FinishedDate is not correct");
    }

    // Priority
    @Test
    public void testPostWithPriorityEqualToZero() {
        task.setPriority(0);
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask response = restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
    }

    @Test
    public void testPostWithPriorityEqualToFive() {
        task.setPriority(5);
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask response = restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
    }

    @Test
    public void testPostWithPriorityLowerThanZero() {
        task.setPriority(-1);
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class));
    }

    @Test
    public void testPostWithPriorityGreaterThanFive() {
        task.setPriority(6);
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class));
    }

    // Dificulty
    @Test
    public void testPostWithWrongDifficulty() {
        task.setDifficulty("WRONG");
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class));
    }

    @Test
    public void testPostWithLowerDifficulty() {
        task.setDifficulty("i want to die");
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask response = restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
        assertEquals(Difficulty.I_WANT_TO_DIE, response.getDifficulty(), "Difficulty is not correct");
    }








}
