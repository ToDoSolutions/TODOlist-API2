package com.todolist.controllers.task;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.dtos.Difficulty;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.Status;
import com.todolist.entity.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// @FlywayTest(additionalLocations = "db/testWithOutData", value = @DataSource(url = "jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
@FlywayTest(additionalLocations = "db/testWithOutData", value = @DataSource(url = "jdbc:mysql://uqiweqtspt5rb4xp:uWHt8scUWIMHRDzt7HCg@b8iyr7xai8wk75ismpbt-mysql.services.clever-cloud.com:3306/b8iyr7xai8wk75ismpbt", username = "uqiweqtspt5rb4xp", password = "uWHt8scUWIMHRDzt7HCg"))
class PostTest {

    Task task;
    // String uri = "http://localhost:8080/api/v1";
    String uri = "https://todolist-api2.herokuapp.com/api/v1";
    RestTemplate restTemplate;

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
        task.setIdTask(0L);
        restTemplate = new RestTemplate();
    }

    // Correct
    @Test
    void testPostFine() {
        ShowTask response = restTemplate.postForObject(uri + "/tasks", task, ShowTask.class);
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
        response = restTemplate.getForObject(uri + "/tasks/1", ShowTask.class);
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
    }

    // Title
    @Test
    void testPostWithNullOrEmptyTitle() {
        task.setTitle(null);
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/tasks", task, ShowTask.class));
        task.setTitle("");
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/tasks", task, ShowTask.class));
    }

    @Test
    void testPostWithTitleGreaterThan50() {
        task.setTitle(new String(new char[51]).replace("\0", "a"));
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/tasks", task, ShowTask.class));
    }

    // Description
    @Test
    void testPostWithNullOrEmptyDescription() {
        task.setDescription(null);
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/tasks", task, ShowTask.class));
        task.setDescription("");
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri, task, ShowTask.class));
    }

    @Test
    void testPostWithDescriptionGreaterThan200() {
        task.setDescription(new String(new char[201]).replace("\0", "a"));
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/tasks", task, ShowTask.class));
    }

    // Annotation
    @Test
    void testPostWithAnnotationGreaterThan50() {
        task.setAnnotation(new String(new char[51]).replace("\0", "a"));
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/tasks", task, ShowTask.class));
    }

    // Status
    @Test
    void testPostWithWrongStatus() {
        task.setStatus("WRONG");
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/tasks", task, ShowTask.class));
    }

    @Test
    void testPostWithLowerStatus() {
        task.setStatus("in progress");
        ShowTask response = restTemplate.postForObject(uri + "/tasks", task, ShowTask.class);
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
        assertEquals(Status.IN_PROGRESS, response.getStatus(), "Status is not correct");
    }

    // Relation between StartDate and FinishedDate
    @Test
    void testPostWithStartDateIsAfterFinishedDate() {
        task.setStartDate("3001-01-30");
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/tasks", task, ShowTask.class));
    }

    // StartDate
    @Test
    void testPostWithWrongPatternInStartDate() {
        task.setStartDate("2015/01/30");
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/tasks", task, ShowTask.class));
    }


    @Test
    void testPostWithNullStartDate() {
        task.setStartDate(null);
        ShowTask response = restTemplate.postForObject(uri + "/tasks", task, ShowTask.class);
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
        assertEquals(LocalDate.now(), response.getStartDate(), "StartDate is not correct");
    }

    // FinishedDate
    @Test
    void testPostWithWrongPatternInFinishedDate() {
        task.setFinishedDate("2015/01/30");
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri +"/tasks", task, ShowTask.class));
    }

    @Test
    void testPostWithNullFinishedDate() {
        task.setFinishedDate(null);
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/tasks", task, ShowTask.class));
    }

    @Test
    void testPostWithFinishedDateIsBeforeCurrentDate() {
        task.setFinishedDate(LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_DATE));
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/tasks", task, ShowTask.class));
    }

    // Priority
    @Test
    void testPostWithPriorityEqualToZero() {
        task.setPriority(0);
        ShowTask response = restTemplate.postForObject(uri + "/tasks", task, ShowTask.class);
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
    }

    @Test
    void testPostWithPriorityEqualToFive() {
        task.setPriority(5);
        ShowTask response = restTemplate.postForObject(uri + "/tasks", task, ShowTask.class);
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
    }

    @Test
    void testPostWithPriorityLowerThanZero() {
        task.setPriority(-1);
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/tasks", task, ShowTask.class));
    }

    @Test
    void testPostWithPriorityGreaterThanFive() {
        task.setPriority(6);
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/tasks", task, ShowTask.class));
    }

    // Dificulty
    @Test
    void testPostWithWrongDifficulty() {
        task.setDifficulty("WRONG");
        assertThrows(HttpClientErrorException.class, () -> restTemplate.postForObject(uri + "/tasks", task, ShowTask.class));
    }

    @Test
    void testPostWithLowerDifficulty() {
        task.setDifficulty("i want to die");
        ShowTask response = restTemplate.postForObject(uri + "/tasks", task, ShowTask.class);
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
        assertEquals(Difficulty.I_WANT_TO_DIE, response.getDifficulty(), "Difficulty is not correct");
    }
}
