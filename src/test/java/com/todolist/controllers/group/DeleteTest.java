package com.todolist.controllers.group;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.TODOlistApplication;
import com.todolist.dtos.ShowGroup;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import com.todolist.entity.User;
import com.todolist.exceptions.ManagerException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@FlywayTest(additionalLocations = "db/migration", value = @DataSource(url = "jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
// @FlywayTest(additionalLocations = "db/migration", value = @DataSource(url = "jdbc:mysql://uqiweqtspt5rb4xp:uWHt8scUWIMHRDzt7HCg@b8iyr7xai8wk75ismpbt-mysql.services.clever-cloud.com:3306/b8iyr7xai8wk75ismpbt", username = "uqiweqtspt5rb4xp", password = "uWHt8scUWIMHRDzt7HCg"))
class DeleteTest {

    String uri = "http://localhost:8080/api/v1/";
    // String uri = "https://todolist-api2.herokuapp.com/api/v1";
    RestTemplate restTemplate;

    @BeforeAll
    public static void beforeClass() {
        TODOlistApplication.main(new String[]{});
    }

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
    }

    /* --- GROUP --- */
    @Test
    void testDeleteGroupFine() {
        ShowGroup response = restTemplate.exchange(uri + "/groups/1", HttpMethod.DELETE, null, ShowGroup.class).getBody();
        assertEquals(1, response.getIdGroup(), "IdGroup is not correct");
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/groups/1", ShowGroup.class)))
                .assertMsg("The group with idGroup 1 does not exist.")
                .assertStatus("Not Found")
                .assertPath("/api/v1/groups/1");
    }

    @Test
    void testDeleteGroupWithWrongId() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/groups/0", HttpMethod.DELETE, null, ShowGroup.class)))
                .assertMsg("The group with idGroup 0 does not exist.")
                .assertStatus("Not Found")
                .assertPath("/api/v1/groups/0");
    }

    /* --- USER --- */
    @Test
    void testDeleteUserFine() {
        ShowGroup response = restTemplate.getForObject(uri + "/groups/1", ShowGroup.class);
        assertTrue(response.getUsers().stream().anyMatch(user -> user.getIdUser() == 1), "User with idUser 1 does not exist.");
        response = restTemplate.exchange(uri + "/groups/1/users/1", HttpMethod.DELETE, null, ShowGroup.class).getBody();
        assertEquals(1, response.getIdGroup(), "IdGroup is not correct");
        assertTrue(response.getUsers().stream().allMatch(user -> user.getIdUser() != 1), "User with idUser 1 exist.");
        ShowUser response2 = restTemplate.getForObject(uri + "/users/1", ShowUser.class);
        assertEquals(1, response2.getIdUser(), "IdUser is not correct");
    }

    @Test
    void testDeleteUserWithWrongId() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/groups/1/users/0", HttpMethod.DELETE, null, ShowGroup.class)))
                .assertMsg("The user with idUser 0 does not exist.")
                .assertStatus("Not Found")
                .assertPath("/api/v1/groups/1/users/0");
    }

    @Test
    void testDeleteUserWithWrongIdGroup() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/groups/0/users/1", HttpMethod.DELETE, null, ShowGroup.class)))
                .assertMsg("The group with idGroup 0 does not exist.")
                .assertStatus("Not Found")
                .assertPath("/api/v1/groups/0/users/1");
    }

    @Test
    void testDeleteAllUsersFine() {
        ShowGroup response = restTemplate.exchange(uri + "/groups/1/users", HttpMethod.DELETE, null, ShowGroup.class).getBody();
        assertEquals(1, response.getIdGroup(), "IdGroup is not correct");
        assertTrue(response.getUsers().isEmpty(), "Users is not empty.");
    }

    @Test
    void testDeleteAllUsersWithWrongIdGroup() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/groups/0/users", HttpMethod.DELETE, null, ShowGroup.class)))
                .assertMsg("The group with idGroup 0 does not exist.")
                .assertStatus("Not Found")
                .assertPath("/api/v1/groups/0/users");
    }
    /* --- TASK --- */
    @Test
    void testDeleteTaskFine() {
        ShowGroup response = restTemplate.getForObject(uri + "/groups/1", ShowGroup.class);
        assertTrue(response.getUsers().stream().flatMap(user -> user.getTasks().stream()).anyMatch(task -> task.getIdTask() == 1), "Task with idTask 1 does not exist.");
        response = restTemplate.exchange(uri + "/groups/1/tasks/1", HttpMethod.DELETE, null, ShowGroup.class).getBody();
        assertEquals(1, response.getIdGroup(), "IdGroup is not correct");
        assertTrue(response.getUsers().stream().flatMap(user -> user.getTasks().stream()).allMatch(task -> task.getIdTask() != 1), "Task with idTask 1 exist.");
        ShowTask response2 = restTemplate.getForObject(uri + "/tasks/1", ShowTask.class);
        assertEquals(1, response2.getIdTask(), "IdTask is not correct");
    }

    @Test
    void testDeleteTaskWithWrongId() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/groups/1/tasks/0", HttpMethod.DELETE, null, ShowGroup.class)))
                .assertMsg("The task with idTask 0 does not exist.")
                .assertStatus("Not Found")
                .assertPath("/api/v1/groups/1/tasks/0");
    }

    @Test
    void testDeleteTaskWithWrongIdGroup() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/groups/0/tasks/1", HttpMethod.DELETE, null, ShowGroup.class)))
                .assertMsg("The group with idGroup 0 does not exist.")
                .assertStatus("Not Found")
                .assertPath("/api/v1/groups/0/tasks/1");
    }
}
