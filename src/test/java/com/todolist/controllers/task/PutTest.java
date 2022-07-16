package com.todolist.controllers.task;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.dtos.Difficulty;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.Status;
import com.todolist.entity.Task;
import com.todolist.exceptions.ManagerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@FlywayTest(additionalLocations = "db/testWithOutData", value = @DataSource(url = "jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
// @FlywayTest(additionalLocations = "db/testWithOutData", value = @DataSource(url = "jdbc:mysql://uqiweqtspt5rb4xp:uWHt8scUWIMHRDzt7HCg@b8iyr7xai8wk75ismpbt-mysql.services.clever-cloud.com:3306/b8iyr7xai8wk75ismpbt", username = "uqiweqtspt5rb4xp", password = "uWHt8scUWIMHRDzt7HCg"))
class PutTest {

    Task task;
    ShowTask showTask;
    String uri = "http://localhost:8080/api/v1";
    // String uri = "https://todolist-api2.herokuapp.com/api/v1";
    RestTemplate restTemplate = new RestTemplate();

    @BeforeEach
    public void setUp() {
        task = Task.of("Task 1", "Task 1 description", "Task 1 annotation", "DRAFT", "3000-01-22", "2015-01-01", 1L, "EASY");
        restTemplate = new RestTemplate();
        showTask = restTemplate.postForObject(uri + "/tasks", task, ShowTask.class);
        task.setIdTask(showTask.getIdTask());
    }

    // Correct
    @Test
    void testPutFine() {
        task = Task.of("Task 2", "Task 2 description", "Task 2 annotation", "DONE",
                "3000-01-22", "2015-01-01", 1L, "EASY");
        task.setIdTask(showTask.getIdTask());
        ShowTask response = restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
        response = restTemplate.getForObject(uri + "/tasks/1", ShowTask.class);
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
    }

    // Not exist
    @Test
    void testPutNotExists() {
        task = Task.of("Task 2", "Task 2 description", "Task 2 annotation", "DONE",
                "3000-01-22", "2015-01-01", 1L, "EASY");
        task.setIdTask(99L);
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class)))
                .assertMsg("The task with idTask " + task.getIdTask() + " does not exist.")
                .assertStatus("Not Found")
                .assertPath("/api/v1/tasks");


    }

    // Title
    @Test
    void testPutWithNullOrEmptyTitle() {
        task.setTitle(null);
        ShowTask response = restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertNotNull(response.getTitle(), "Title is null");
        task.setTitle("");
        response = restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertEquals(showTask.getTitle(), response.getTitle(), "Title is empty.");


    }

    @Test
    void testPutWithTitleGreaterThan50() {
        task.setTitle(new String(new char[51]).replace("\0", "a"));
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class)))
                .assertMsg("The title is too long.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/tasks");
    }

    // Description
    @Test
    void testPutWithNullOrEmptyDescription() {
        task.setDescription(null);
        ShowTask response = restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertNotNull(response.getDescription(), "Description is null");
        task.setDescription("");
        response = restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertEquals(showTask.getDescription(), response.getDescription(), "Description is empty.");
    }

    @Test
    void testPutWithDescriptionGreaterThan200() {
        task.setDescription(new String(new char[201]).replace("\0", "a"));
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class)))
                .assertMsg("The description is too long.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/tasks");
    }

    // Annotation
    @Test
    void testPutWithNullOrEmptyAnnotation() {
        task.setAnnotation(null);
        ShowTask response = restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertNotNull(response.getAnnotation(), "Annotation is null");
        task.setAnnotation("");
        response = restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertEquals(showTask.getAnnotation(), response.getAnnotation(), "Annotation is empty.");
    }

    @Test
    void testPutWithAnnotationGreaterThan50() {
        task.setAnnotation(new String(new char[51]).replace("\0", "a"));
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class)))
                .assertMsg("The annotation is too long.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/tasks");
    }

    // Status
    @Test
    void testPutWithNullOrEmptyStatus() {
        task.setStatus(null);
        ShowTask response = restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertNotNull(response.getStatus(), "Status is null");
        task.setStatus("");
        response = restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertEquals(showTask.getStatus(), response.getStatus(), "Status is empty.");
    }

    @Test
    void testPutWithWrongStatus() {
        task.setStatus("WRONG");
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class)))
                .assertMsg("The status WRONG is not valid and it should be one of the following -> draft - in_progress - in_revision - done - cancelled.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/tasks");
    }

    @Test
    void testPutWithLowerStatus() {
        task.setStatus("in progress");
        ShowTask response = restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
        assertEquals(Status.IN_PROGRESS, response.getStatus(), "Status is not correct");
    }

    // Relation between StartDate and FinishedDate
    @Test
    void testPutWithStartDateIsAfterFinishedDate() {
        task.setStartDate("3001-01-30");
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class)))
                .assertMsg("The startDate is must be before the finishedDate.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/tasks");
    }

    // StartDate
    @Test
    void testPutWithNullOrEmptyStartDate() {
        task.setStartDate(null);
        ShowTask response = restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertNotNull(response.getStartDate(), "StartDate is null");
        task.setStartDate("");
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class)))
                .assertMsg("The startDate must be in format yyyy-MM-dd.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/tasks");
        assertEquals(showTask.getStartDate(), response.getStartDate(), "StartDate is empty.");
    }

    @Test
    void testPutWithWrongPatternInStartDate() {
        task.setStartDate("2015/01/30");
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class)))
                .assertMsg("The startDate must be in format yyyy-MM-dd.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/tasks");
    }

    @Test
    void testPutWithNullStartDate() {
        task.setStartDate(null);
        ShowTask response = restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
        assertEquals(showTask.getStartDate(), response.getStartDate(), "StartDate is not correct");
    }

    // FinishedDate
    @Test
    void testPutWithNullOrEmptyFinishedDate() {
        task.setFinishedDate(null);
        ShowTask response = restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertNotNull(response.getFinishedDate(), "FinishedDate is null");
        task.setFinishedDate("");
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class)))
                .assertMsg("The finishedDate must be in format yyyy-MM-dd.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/tasks");
        assertEquals(showTask.getFinishedDate(), response.getFinishedDate(), "FinishedDate is empty.");
    }

    @Test
    void testPutWithWrongPatternInFinishedDate() {
        task.setFinishedDate("2015/01/30");
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class)))
                .assertMsg("The finishedDate must be in format yyyy-MM-dd.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/tasks");
    }

    @Test
    void testPutWithNullFinishedDate() {
        task.setFinishedDate(null);
        ShowTask response = restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertEquals(showTask.getFinishedDate(), response.getFinishedDate(), "FinishedDate is not correct");
    }

    // Priority
    @Test
    void testPutWithNullPriority() {
        task.setPriority(null);
        ShowTask response = restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertNotNull(response.getPriority(), "Priority is null");
    }

    @Test
    void testPutWithPriorityEqualToZero() {
        task.setPriority(0L);
        ShowTask response = restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
    }

    @Test
    void testPutWithPriorityEqualToFive() {
        task.setPriority(5L);
        ShowTask response = restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
    }

    @Test
    void testPutWithPriorityLowerThanZero() {
        task.setPriority(-1L);
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class)))
                .assertMsg("The priority must be between 0 and 5.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/tasks");
    }

    @Test
    void testPutWithPriorityGreaterThanFive() {
        task.setPriority(6L);
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class)))
                .assertMsg("The priority must be between 0 and 5.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/tasks");
    }

    // Difficulty
    @Test
    void testPutWithNullOrEmptyDifficulty() {
        task.setDifficulty(null);
        ShowTask response = restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertNotNull(response.getDifficulty(), "Difficulty is null");
        task.setDifficulty("");
        response = restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertEquals(showTask.getDifficulty(), response.getDifficulty(), "Difficulty is empty.");
    }

    @Test
    void testPutWithWrongDifficulty() {
        task.setDifficulty("WRONG");
        //HttpClientErrorException hola = assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class));
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class)))
                .assertMsg("The difficulty WRONG is not valid and it should be one of the following -> sleep - easy - medium - hard - hardcore - i_want_to_die.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/tasks");
    }

    @Test
    void testPutWithLowerDifficulty() {
        task.setDifficulty("i want to die");
        ShowTask response = restTemplate.exchange(uri + "/tasks", HttpMethod.PUT, new HttpEntity<>(task), ShowTask.class).getBody();
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
        assertEquals(Difficulty.I_WANT_TO_DIE, response.getDifficulty(), "Difficulty is not correct");
    }
}
