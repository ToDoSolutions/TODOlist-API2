package com.todolist.controllers.task;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.dtos.Difficulty;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.Status;
import com.todolist.entity.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@FlywayTest(additionalLocations = "db/testWithOutData", value = @DataSource(url = "jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
class PutTest {

    Task task;
    ShowTask showTask;
    String uri = "http://localhost:8080/api/v1/tasks";

    @BeforeEach
    public void setUp() {
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
        RestTemplate restTemplate = new RestTemplate();
        showTask = restTemplate.postForObject(uri, task, ShowTask.class);
    }

    // Correct
    @Test
    void testPutFine() {
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
        ShowTask response = restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
        String uri2 = "http://localhost:8080/api/v1/tasks/1";
        response = restTemplate.getForObject(uri2, ShowTask.class);
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
    }

    // Not exist
    @Test
    void testPutNotExists() {
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
        RestTemplate restTemplate = new RestTemplate();
        task.setTitle("");
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class));
    }

    @Test
    void testPostWithTitleGreaterThan50() {
        task.setTitle(new String(new char[51]).replace("\0", "a"));
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class));
    }

    // Description
    @Test
    void testPostWithEmptyDescription() {
        RestTemplate restTemplate = new RestTemplate();
        task.setDescription("");
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class));
    }

    @Test
    void testPostWithDescriptionGreaterThan200() {
        task.setDescription(new String(new char[201]).replace("\0", "a"));
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class));
    }

    // Annotation
    @Test
    void testPostWithAnnotationGreaterThan50() {
        task.setAnnotation(new String(new char[51]).replace("\0", "a"));
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class));
    }

    // Status
    @Test
    void testPostWithWrongStatus() {
        task.setStatus("WRONG");
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class));
    }

    @Test
    void testPostWithLowerStatus() {
        task.setStatus("in progress");
        RestTemplate restTemplate = new RestTemplate();
        ShowTask response = restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
        assertEquals(Status.IN_PROGRESS, response.getStatus(), "Status is not correct");
    }

    // Relation between StartDate and FinishedDate
    @Test
    void testPostWithStartDateIsAfterFinishedDate() {
        task.setStartDate("3001-01-30");
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class));
    }

    // StartDate
    @Test
    void testPostWithWrongPatternInStartDate() {
        task.setStartDate("2015/01/30");
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class));
    }

    @Test
    void testPostWithNullStartDate() {
        task.setStartDate(null);
        RestTemplate restTemplate = new RestTemplate();
        ShowTask response = restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
        assertEquals(showTask.getStartDate(), response.getStartDate(), "StartDate is not correct");
    }

    // FinishedDate
    @Test
    void testPostWithWrongPatternInFinishedDate() {
        task.setFinishedDate("2015/01/30");
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class));
    }

    @Test
    void testPostWithNullFinishedDate() {
        task.setFinishedDate(null);
        RestTemplate restTemplate = new RestTemplate();
        ShowTask response = restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertEquals(showTask.getFinishedDate(), response.getFinishedDate(), "FinishedDate is not correct");
    }

    // Priority
    @Test
    void testPostWithPriorityEqualToZero() {
        task.setPriority(0);
        RestTemplate restTemplate = new RestTemplate();
        ShowTask response = restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
    }

    @Test
    void testPostWithPriorityEqualToFive() {
        task.setPriority(5);
        RestTemplate restTemplate = new RestTemplate();
        ShowTask response = restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
    }

    @Test
    void testPostWithPriorityLowerThanZero() {
        task.setPriority(-1);
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class));
    }

    @Test
    void testPostWithPriorityGreaterThanFive() {
        task.setPriority(6);
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class));
    }

    // Dificulty
    @Test
    void testPostWithWrongDifficulty() {
        task.setDifficulty("WRONG");
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class));
    }

    @Test
    void testPostWithLowerDifficulty() {
        task.setDifficulty("i want to die");
        RestTemplate restTemplate = new RestTemplate();
        ShowTask response = restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
        assertEquals(Difficulty.I_WANT_TO_DIE, response.getDifficulty(), "Difficulty is not correct");
    }


}
