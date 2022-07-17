package com.todolist.controllers.user;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import com.todolist.exceptions.ManagerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@FlywayTest(additionalLocations = "db/migration", value = @DataSource(url = "jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
// @FlywayTest(additionalLocations = "db/migration", value = @DataSource(url = "jdbc:mysql://uqiweqtspt5rb4xp:uWHt8scUWIMHRDzt7HCg@b8iyr7xai8wk75ismpbt-mysql.services.clever-cloud.com:3306/b8iyr7xai8wk75ismpbt", username = "uqiweqtspt5rb4xp", password = "uWHt8scUWIMHRDzt7HCg"))
class GetSoloTest {

    String uri = "http://localhost:8080/api/v1";
    // String uri = "https://todolist-api2.herokuapp.com/api/v1";
    RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
    }

    @Test
    void testGetSoloFine() {
        ShowUser response = restTemplate.getForObject(uri + "/users/1", ShowUser.class);
        assertEquals(1, response.getIdUser(), "IdUser is not correct");
    }

    @Test
    void testGetSoloFields() {
        ShowUser response = restTemplate.getForObject(uri + "/users/1?fieldsUser=idUser,name,surname", ShowUser.class);
        assertEquals(1, response.getIdUser(), "IdUser is not correct");
        assertNull(response.getTasks(), "Tasks is not correct");
    }

    @Test
    void testGetSoloFieldsWithWrongField() {
        ManagerException exception = ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/users/1?fieldsUser=idUser,wrongField", ShowTask.class)));
        assertEquals("Bad Request", exception.getStatus(), "Status is not correct");
        assertEquals("The users' fields are invalid.", exception.getMsg(), "Message is not correct");
        assertEquals("/api/v1/users/1", exception.getPath(), "Code is not correct");

    }

    @Test
    void testGetSoloUpperFields() {
        ShowUser response = restTemplate.getForObject(uri + "/users/1?fieldsUser=IDUSER,NAME,SURNAME", ShowUser.class);
        assertEquals(1, response.getIdUser(), "IdUser is not correct");
        assertNull(response.getTasks(), "Tasks is not correct");
    }


    @Test
    void testGetSoloNotFound() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/users/99", ShowUser.class)))
                .assertMsg("The user with idUser 99 does not exist.")
                .assertStatus("Not Found")
                .assertPath("/api/v1/users/99");
    }
}
