package com.todolist.resources.task;

import com.todolist.dtos.Difficulty;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.Status;
import com.todolist.entity.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PostTest {

    Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setTitle("Task 1");
        task.setDescription("Task 1 description");
        task.setAnnotation("Task 1 annotation");
        task.setStatus("DRAFT");
        task.setFinishedDate("3000-01-22");
        task.setStartDate("2015-01-01");
        task.setDifficulty("EASY");
        task.setPriority(1);
        task.setIdTask(0);
    }

    // Correct
    @Test
    void testPostFine() {
        String uri1 = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask response = restTemplate.postForObject(uri1, task, ShowTask.class);
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
        String uri2 = "http://localhost:8080/api/v1/tasks/1";
        response = restTemplate.getForObject(uri2, ShowTask.class);
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
    }

    // Title
    @Test
    void testPostWithNullOrEmptyTitle() {
        task.setTitle(null);
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, task, ShowTask.class));
        task.setTitle("");
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, task, ShowTask.class));
    }

    @Test
    void testPostWithTitleGreaterThan50() {
        task.setTitle(new String(new char[51]).replace("\0", "a"));
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, task, ShowTask.class));
    }

    // Description
    @Test
    void testPostWithNullOrEmptyDescription() {
        task.setDescription(null);
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, task, ShowTask.class));
        task.setDescription("");
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, task, ShowTask.class));
    }

    @Test
    void testPostWithDescriptionGreaterThan200() {
        task.setDescription(new String(new char[201]).replace("\0", "a"));
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, task, ShowTask.class));
    }

    // Annotation
    @Test
    void testPostWithAnnotationGreaterThan50() {
        task.setAnnotation(new String(new char[51]).replace("\0", "a"));
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, task, ShowTask.class));
    }

    // Status
    @Test
    void testPostWithWrongStatus() {
        task.setStatus("WRONG");
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, task, ShowTask.class));
    }

    @Test
    void testPostWithLowerStatus() {
        task.setStatus("in progress");
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask response = restTemplate.postForObject(uri, task, ShowTask.class);
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
        assertEquals(Status.IN_PROGRESS, response.getStatus(), "Status is not correct");
    }

    // Relation between StartDate and FinishedDate
    @Test
    void testPostWithStartDateIsAfterFinishedDate() {
        task.setStartDate("3001-01-30");
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, task, ShowTask.class));
    }

    // StartDate
    @Test
    void testPostWithWrongPatternInStartDate() {
        task.setStartDate("2015/01/30");
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, task, ShowTask.class));
    }


    @Test
    void testPostWithNullStartDate() {
        task.setStartDate(null);
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask response = restTemplate.postForObject(uri, task, ShowTask.class);
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
        assertEquals(LocalDate.now(), response.getStartDate(), "StartDate is not correct");
    }

    // FinishedDate
    @Test
    void testPostWithWrongPatternInFinishedDate() {
        task.setFinishedDate("2015/01/30");
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, task, ShowTask.class));
    }

    @Test
    void testPostWithNullFinishedDate() {
        task.setFinishedDate(null);
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, task, ShowTask.class));
    }

    @Test
    void testPostWithFinishedDateIsBeforeCurrentDate() {
        task.setFinishedDate(LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_DATE));
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, task, ShowTask.class));
    }

    // Priority
    @Test
    void testPostWithPriorityEqualToZero() {
        task.setPriority(0);
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask response = restTemplate.postForObject(uri, task, ShowTask.class);
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
    }

    @Test
    void testPostWithPriorityEqualToFive() {
        task.setPriority(5);
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask response = restTemplate.postForObject(uri, task, ShowTask.class);
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
    }

    @Test
    void testPostWithPriorityLowerThanZero() {
        task.setPriority(-1);
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, task, ShowTask.class));
    }

    @Test
    void testPostWithPriorityGreaterThanFive() {
        task.setPriority(6);
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, task, ShowTask.class));
    }

    // Dificulty
    @Test
    void testPostWithWrongDifficulty() {
        task.setDifficulty("WRONG");
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, task, ShowTask.class));
    }

    @Test
    void testPostWithLowerDifficulty() {
        task.setDifficulty("i want to die");
        String uri = "http://localhost:8080/api/v1/tasks";
        RestTemplate restTemplate = new RestTemplate();
        ShowTask response = restTemplate.postForObject(uri, task, ShowTask.class);
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
        assertEquals(Difficulty.I_WANT_TO_DIE, response.getDifficulty(), "Difficulty is not correct");
    }
}
