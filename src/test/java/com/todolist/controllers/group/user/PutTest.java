package com.todolist.controllers.group.user;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.TODOlistApplication;
import com.todolist.dtos.ShowGroup;
import com.todolist.dtos.ShowUser;
import com.todolist.exceptions.ManagerException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@FlywayTest(additionalLocations = "db/migration", value = @DataSource(url = "jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
// @FlywayTest(additionalLocations = "db/migration", value = @DataSource(url = "jdbc:mysql://uqiweqtspt5rb4xp:uWHt8scUWIMHRDzt7HCg@b8iyr7xai8wk75ismpbt-mysql.services.clever-cloud.com:3306/b8iyr7xai8wk75ismpbt", username = "uqiweqtspt5rb4xp", password = "uWHt8scUWIMHRDzt7HCg"))
class PutTest {

    String uri = "http://localhost:8080/api/v1";
    // String uri = "https://todolist-api2.herokuapp.com/api/v1";
    RestTemplate restTemplate;

    @BeforeAll
    public static void beforeClass() {
        TODOlistApplication.main(new String[]{});
    }

    @Test
    void testPostFine() {
        ShowUser response1 = restTemplate.getForObject(uri + "/group/1", ShowUser.class);
        ShowUser response2 = restTemplate.exchange(uri + "/group/1/user/2", HttpMethod.PUT, null, ShowUser.class).getBody();
        assertEquals(response1.getTasks().size() + 1, response2.getTasks().size(), "The number of tasks is not correct");
    }

    @Test
    void testPostAlreadyAdded() {
        ShowGroup response1 = restTemplate.getForObject(uri + "/group/1", ShowGroup.class);
        ShowGroup response2 = restTemplate.exchange(uri + "/group/1/user/1", HttpMethod.PUT, null, ShowGroup.class).getBody();
        assertEquals(response1.getNumTasks(), response2.getNumTasks(), "The number of tasks is not correct");
    }

    @Test
    void testPostWithNotExistTask() {

        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/group/1/user/-1", HttpMethod.PUT, null, ShowGroup.class)))
                .assertMsg("The user with idUser -1 does not exist.")
                .assertStatus("Not Found")
                .assertPath("/api/v1/group/1/user/-1");
    }

    @Test
    void testPostWithNotExistUser() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/group/-1/user/2", HttpMethod.PUT, null, ShowGroup.class)))
                .assertMsg("The group with idGroup -1 does not exist.")
                .assertStatus("Not Found")
                .assertPath("/api/v1/group/-1/user/2");
    }
}
