package com.todolist.controllers.group;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.dtos.ShowGroup;
import com.todolist.exceptions.ManagerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@FlywayTest(additionalLocations = "db/testWithData", value = @DataSource(url = "jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
// @FlywayTest(additionalLocations = "db/testWithData", value = @DataSource(url = "jdbc:mysql://uqiweqtspt5rb4xp:uWHt8scUWIMHRDzt7HCg@b8iyr7xai8wk75ismpbt-mysql.services.clever-cloud.com:3306/b8iyr7xai8wk75ismpbt", username = "uqiweqtspt5rb4xp", password = "uWHt8scUWIMHRDzt7HCg"))
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
        ShowGroup response = restTemplate.getForObject(uri + "/groups/1", ShowGroup.class);
        assertEquals(1, response.getIdGroup(), "IdGroup is not correct");
    }

    @Test
    void testGetSoloFields() {
        ShowGroup response = restTemplate.getForObject(uri + "/groups/1?fieldsGroup=idGroup,name,description", ShowGroup.class);
        assertEquals(1, response.getIdGroup(), "IdGroup is not correct");
        assertNull(response.getUsers(), "Users is not correct");
    }

    @Test
    void testGetSoloFieldsWithWrongField() {
        RestTemplate restTemplate = new RestTemplate();
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/groups/1?fieldsGroup=idGroup,wrongField", ShowGroup.class)))
                .assertMsg("The groups' fields are invalid.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/groups/1");
    }

    @Test
    void testGetSoloUpperFields() {
        ShowGroup response = restTemplate.getForObject(uri + "/groups/1?fieldsGroup=IDGROUP,NAME,DESCRIPTION", ShowGroup.class);
        assertEquals(1, response.getIdGroup(), "IdGroup is not correct");
        assertNull(response.getUsers(), "Users is not correct");
    }


    @Test
    void testGetSoloNotFound() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/groups/99", ShowGroup.class)))
                .assertMsg("The group with idGroup 99 does not exist.")
                .assertStatus("Not Found")
                .assertPath("/api/v1/groups/99");
    }
}
