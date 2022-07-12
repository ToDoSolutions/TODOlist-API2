package com.todolist.controllers.group;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.dtos.ShowGroup;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@FlywayTest(additionalLocations = "db/testWithData", value = @DataSource(url = "jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
class GetSoloTest {

    @Test
    void testGetSoloFine() {
        String uri = "http://localhost:8080/api/v1/groups/1";
        RestTemplate restTemplate = new RestTemplate();
        ShowGroup response = restTemplate.getForObject(uri, ShowGroup.class);
        assertEquals(1, response.getIdGroup(), "IdGroup is not correct");
    }

    @Test
    void testGetSoloFields() {
        String uri = "http://localhost:8080/api/v1/groups/1?fieldsGroup=idGroup,name,description";
        RestTemplate restTemplate = new RestTemplate();
        ShowGroup response = restTemplate.getForObject(uri, ShowGroup.class);
        assertEquals(1, response.getIdGroup(), "IdGroup is not correct");
        assertEquals(null, response.getUsers(), "Users is not correct");
    }

    @Test
    void testGetSoloFieldsWithWrongField() {
        String uri = "http://localhost:8080/api/v1/groups/1?fieldsGroup=idGroup,wrongField";
        RestTemplate restTemplate = new RestTemplate();
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri, ShowGroup.class));
        assertEquals("404", exception.getMessage().split(":")[0].trim(), "Status code is not correct");
    }

    @Test
    void testGetSoloUpperFields() {
        String uri = "http://localhost:8080/api/v1/groups/1?fieldsGroup=IDGROUP,NAME,DESCRIPTION";
        RestTemplate restTemplate = new RestTemplate();
        ShowGroup response = restTemplate.getForObject(uri, ShowGroup.class);
        assertEquals(1, response.getIdGroup(), "IdGroup is not correct");
        assertEquals(null, response.getUsers(), "Users is not correct");
    }


    @Test
    void testGetSoloNotFound() {
        String uri = "http://localhost:8080/api/v1/groups/99";
        RestTemplate restTemplate = new RestTemplate();
        Throwable exception = assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri, ShowGroup.class));
        assertEquals("404", exception.getMessage().split(":")[0].trim(), "Status code is not correct");
    }
}