package com.todolist.controllers.user;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.config.errors.ManagerException;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@FlywayTest(additionalLocations = "db/testWithData", value = @DataSource(url = "jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
class GetSoloTest {

    @Test
    void testGetSoloFine() {
        String uri = "http://localhost:8080/api/v1/users/1";
        RestTemplate restTemplate = new RestTemplate();
        ShowUser response = restTemplate.getForObject(uri, ShowUser.class);
        assertEquals(1, response.getIdUser(), "IdUser is not correct");
    }

    @Test
    void testGetSoloFields() {
        String uri = "http://localhost:8080/api/v1/users/1?fieldsUser=idUser,name,surname";
        RestTemplate restTemplate = new RestTemplate();
        ShowUser response = restTemplate.getForObject(uri, ShowUser.class);
        assertEquals(1, response.getIdUser(), "IdUser is not correct");
        assertEquals(null, response.getTasks(), "Tasks is not correct");
    }

    @Test
    void testGetSoloFieldsWithWrongField() {
        String uri = "http://localhost:8080/api/v1/users/1?fieldsUser=idUser,wrongField";
        RestTemplate restTemplate = new RestTemplate();
        ManagerException exception = new ManagerException(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri, ShowTask.class)));
        assertEquals("Bad Request", exception.getStatus(), "Status is not correct");
        assertEquals("The users' fields are invalid.", exception.getMsg(), "Message is not correct");
        assertEquals("/api/v1/users/1", exception.getPath(), "Code is not correct");

    }

    @Test
    void testGetSoloUpperFields() {
        String uri = "http://localhost:8080/api/v1/users/1?fieldsUser=IDUSER,NAME,SURNAME";
        RestTemplate restTemplate = new RestTemplate();
        ShowUser response = restTemplate.getForObject(uri, ShowUser.class);
        assertEquals(1, response.getIdUser(), "IdUser is not correct");
        assertEquals(null, response.getTasks(), "Tasks is not correct");
    }


    @Test
    void testGetSoloNotFound() {
        String uri = "http://localhost:8080/api/v1/users/99";
        RestTemplate restTemplate = new RestTemplate();
        ManagerException exception = new ManagerException(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri, ShowUser.class)));
        assertEquals("Not Found", exception.getStatus(), "Status code is not correct");
        assertEquals("The user with idUser 99 does not exist.", exception.getMsg(), "Message is not correct");
    }
}
