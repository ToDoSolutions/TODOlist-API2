package com.todolist.controllers.task;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.config.errors.ManagerException;
import com.todolist.dtos.ShowTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// @FlywayTest(additionalLocations = "db/testWithData", value = @DataSource(url = "jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
@FlywayTest(additionalLocations = "db/testWithData", value = @DataSource(url = "jdbc:mysql://uqiweqtspt5rb4xp:uWHt8scUWIMHRDzt7HCg@b8iyr7xai8wk75ismpbt-mysql.services.clever-cloud.com:3306/b8iyr7xai8wk75ismpbt", username = "uqiweqtspt5rb4xp", password = "uWHt8scUWIMHRDzt7HCg"))
class DeleteTest {

    // String uri = "http://localhost:8080/api/v1/";
    String uri = "https://todolist-api2.herokuapp.com/api/v1";
    RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
    }

    @Test
    void testDeleteFine() {
        ShowTask response = restTemplate.exchange(uri + "/tasks/1", HttpMethod.DELETE, null, ShowTask.class).getBody();
        assertEquals(1, response.getIdTask(), "IdTask is not correct");
        ManagerException exception = new ManagerException(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/tasks/1", ShowTask.class)));
        assertEquals("Not Found", exception.getStatus(), "Status code is not correct");
        assertEquals("The task with idTask 1 does not exist.", exception.getMsg(), "Message is not correct");
        assertEquals("/api/v1/tasks/1", exception.getPath(), "Path is not correct");
    }

    @Test
    void testDeleteWithWrongId() {
        ManagerException exception = new ManagerException(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/tasks/0", HttpMethod.DELETE, null, ShowTask.class)));
        assertEquals("Not Found", exception.getStatus(), "Status code is not correct");
        assertEquals("The task with idTask 0 does not exist.", exception.getMsg(), "Message is not correct");
        assertEquals("/api/v1/tasks/0", exception.getPath(), "Path is not correct");
    }
}
