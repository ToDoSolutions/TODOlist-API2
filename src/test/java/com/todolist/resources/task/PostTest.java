package com.todolist.resources.task;

import com.todolist.entity.Task;
import com.todolist.utilities.SQL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PostTest {

    Task task;

    @BeforeEach
    public void setUp() {
        SQL.start("jdbc:mariadb://localhost:3306/todolist-api2", "root", "mazetosan$root");
        SQL.read("data/create.sql");
        task = new Task();
        task.setTitle("Task 1");
        task.setDescription("Task 1 description");
        task.setAnnotation("Task 1 annotation");
        task.setStatus("DRAFT");
        task.setFinishedDate("2015-01-22");
        task.setStartDate("2015-01-01");
        task.setDifficulty("EASY");
        task.setPriority(1);
        task.setIdTask(0);
    }

    @Test
    public void testPostFine() {
        String uri1 = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        Task response = restTemplate.postForObject(uri1, task, Task.class);
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
        String uri2 = "http://localhost:8080/api/v1/tasks/1";
        response = restTemplate.getForObject(uri2, Task.class);
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
    }

    @Test
    public void testPostWithNullTitle() {
        task.setTitle(null);
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, task, Task.class));
    }

    @Test
    public void testPostWithTitleGreaterThan50() {
        task.setTitle("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, task, Task.class));
    }

    @Test
    public void testPostWithDescriptionGreaterThan200() {
        task.setDescription("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, task, Task.class));
    }

    @Test
    public void testPostWithAnnouncementGreaterThan50() {
        task.setAnnotation("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, task, Task.class));
    }

    @Test
    public void testPostWithWrongStatus() {
        task.setStatus("WRONG");
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, task, Task.class));
    }

    @Test
    public void testPostWithStartDateIsAfterFinishedDate() {
        task.setStartDate("2015-01-30");
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, task, Task.class));
    }

    @Test
    public void testPostWithWrongPatternInStartDate() {
        task.setStartDate("2015/01/30");
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, task, Task.class));
    }

    @Test
    public void testPostWithWrongPatternInFinishedDate() {
        task.setFinishedDate("2015/01/30");
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, task, Task.class));
    }

    @Test
    public void testPostWithPriorityEqualToZero() {
        task.setPriority(0);
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        Task response = restTemplate.postForObject(uri, task, Task.class);
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
    }

    @Test
    public void testPostWithPriorityEqualToFive() {
        task.setPriority(5);
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        Task response = restTemplate.postForObject(uri, task, Task.class);
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
    }

    @Test
    public void testPostWithPriorityLowerThanZero() {
        task.setPriority(-1);
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, task, Task.class));
    }

    @Test
    public void testPostWithPriorityGreaterThanFive() {
        task.setPriority(6);
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, task, Task.class));
    }

    @Test
    public void testPostWithWrongDifficulty() {
        task.setDifficulty("WRONG");
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, task, Task.class));
    }






}
