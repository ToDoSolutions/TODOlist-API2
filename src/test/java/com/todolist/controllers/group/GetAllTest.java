package com.todolist.controllers.group;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.TODOlistApplication;
import com.todolist.dtos.ShowGroup;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import com.todolist.exceptions.ManagerException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@FlywayTest(additionalLocations = "db/migration", value = @DataSource(url = "jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
// @FlywayTest(additionalLocations = "db/migration", value = @DataSource(url = "jdbc:mysql://uqiweqtspt5rb4xp:uWHt8scUWIMHRDzt7HCg@b8iyr7xai8wk75ismpbt-mysql.services.clever-cloud.com:3306/b8iyr7xai8wk75ismpbt", username = "uqiweqtspt5rb4xp", password = "uWHt8scUWIMHRDzt7HCg"))
class GetAllTest {

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

    // TODO: test getAll normal, with idUser and With idTask.

    @Test
    void testGetAllFine() {
        ShowUser[] response = restTemplate.getForObject(uri + "/groups", ShowUser[].class);
        RestTemplate restTemplate = new RestTemplate();
        assertNotNull(response, "Response is null");
        assertEquals(3, response.length, "Length is not correct");
    }

    // Offset
    @Test
    void testGetAllWithCorrectOffset() {
        ShowUser[] response = restTemplate.getForObject(uri + "/groups?offset=2", ShowUser[].class);
        assertNotNull(response, "Response is null");
        assertEquals(2, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithIncorrectOffset() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/groups?offset=a", ShowTask[].class)))
                .assertMsg("Error while parsing the next string  \\a\\.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/groups");
    }

    @Test
    void testGetAllWithNegativeOffset() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/groups?offset=-1", ShowTask[].class)))
                .assertMsg("The offset must be positive.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/groups");
    }

    // Limit
    @Test
    void testGetAllWithCorrectLimit() {
        ShowUser[] response = restTemplate.getForObject(uri + "/groups?limit=2", ShowUser[].class);
        assertNotNull(response, "Response is null");
        assertEquals(2, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithIncorrectLimit() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/groups?limit=a", ShowUser[].class)))
                .assertMsg("Error while parsing the next string  \\a\\.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/groups");
    }

    @Test
    void testGetAllWithNegativeLimit() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/groups?limit=-2", ShowUser[].class)))
                .assertMsg("The limit must be positive.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/groups");
    }

    // Offset + Limit
    @Test
    void testGetAllWithCorrectOffsetAndLimit() {
        ShowUser[] response = restTemplate.getForObject(uri + "/users?offset=2&limit=2", ShowUser[].class);
        assertNotNull(response, "Response is null");
        assertEquals(2, response.length, "Length is not correct");
    }

    // Order
    @Test
    void testGetAllWithCorrectOrder() {
        ShowUser[] response = restTemplate.getForObject(uri + "/groups?order=name", ShowUser[].class);
        assertNotNull(response, "Response is null");
        assertEquals(3, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithIncorrectOrder() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/groups?order=a", ShowUser[].class)))
                .assertMsg("The order is invalid.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/groups");
    }

    @Test
    void testGetAllWithOrderAndOffset() {
        ShowUser[] response = restTemplate.getForObject(uri + "/groups?order=name&offset=2", ShowUser[].class);
        assertNotNull(response, "Response is null");
        assertEquals(2, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithOrderAndLimit() {
        ShowTask[] response = restTemplate.getForObject(uri + "/groups?order=name&limit=2", ShowTask[].class);
        assertNotNull(response, "Response is null");
        assertEquals(2, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithOrderAndOffsetAndLimit() {
        ShowUser[] response = restTemplate.getForObject(uri + "/groups?order=name&offset=2&limit=2", ShowUser[].class);
        assertNotNull(response, "Response is null");
        assertEquals(2, response.length, "Length is not correct");
    }

    // Task fields
    @Test
    void testGetAllWithCorrectFields() {
        ShowTask[] response = restTemplate.getForObject(uri + "/groups?fieldsTask=title,description", ShowTask[].class);
        assertNotNull(response, "Response is null");
        assertEquals(3, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithIncorrectFields() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () ->restTemplate.getForObject(uri + "/groups?fieldsTask=a", ShowTask[].class)))
                .assertMsg("The tasks' fields are invalid.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/groups");
    }

    // User Fields
    @Test
    void testGetAllWithUserCorrectFields() {
        ShowUser[] response = restTemplate.getForObject(uri + "/groups?fieldsUser=name,surname", ShowUser[].class);
        assertNotNull(response, "Response is null");
        assertEquals(3, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithUserIncorrectFields() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () ->restTemplate.getForObject(uri + "/groups?fieldsUser=a", ShowUser[].class)))
                .assertMsg("The users' fields are invalid.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/groups");
    }

    // Group fields
    @Test
    void testGetAllWithGroupCorrectFields() {
        ShowUser[] response = restTemplate.getForObject(uri + "/groups?fieldsGroup=name", ShowUser[].class);
        assertNotNull(response, "Response is null");
        assertEquals(3, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithGroupIncorrectFields() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () ->restTemplate.getForObject(uri + "/groups?fieldsGroup=a", ShowTask[].class)))
                .assertMsg("The groups' fields are invalid.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/groups");
    }

    // Name
    @Test
    void testGetAllByName() {
        ShowGroup[] response = restTemplate.getForObject(uri + "/groups?name=Pepe", ShowGroup[].class);
        assertNotNull(response, "Response is null");
        assertEquals(1, response.length, "Length is not correct");
    }

    // Description
    @Test
    void testGetAllByDescription() {
        ShowGroup[] response = restTemplate.getForObject(uri + "/groups?description=Solo quieren ver el mundo arder", ShowGroup[].class);
        assertNotNull(response, "Response is null");
        assertEquals(1, response.length, "Length is not correct");
    }

    // Number tasks
    @Test
    void testGetAllEqualNumTasks() {
        ShowGroup[] response = restTemplate.getForObject(uri + "/groups?numTasks==2", ShowGroup[].class);
        assertNotNull(response, "Response is null");
        assertEquals(2, response.length, "Length is not correct");
    }

    @Test
    void testGetAllLowerNumTasks() {
        ShowGroup[] response = restTemplate.getForObject(uri + "/groups?numTasks=>2", ShowGroup[].class);
        assertNotNull(response, "Response is null");
        assertEquals(1, response.length, "Length is not correct");
    }

    @Test
    void testGetAllGreaterNumTasks() {
        ShowGroup[] response = restTemplate.getForObject(uri + "/groups?numTasks=<2", ShowGroup[].class);
        assertNotNull(response, "Response is null");
        assertEquals(0, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithIncorrectNumTasks() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () ->restTemplate.getForObject(uri + "/groups?numTasks=a", ShowGroup[].class)))
                .assertMsg("The filter is not valid and it should have a parameter filter (< - > - = - <= - =< - >= - => - == - != - <> - ><) and a number without decimals.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/groups");
    }

    // Created date
    @Test
    void testGetAllEqualCreatedDate() {
        ShowGroup[] response = restTemplate.getForObject(uri + "/groups?createdDate==2006-10-12", ShowGroup[].class);
        assertNotNull(response, "Response is null");
        assertEquals(1, response.length, "Length is not correct");
    }

    @Test
    void testGetAllLowerCreatedDate() {
        ShowGroup[] response = restTemplate.getForObject(uri + "/groups?createdDate=<2006-10-12", ShowGroup[].class);
        assertNotNull(response, "Response is null");
        assertEquals(1, response.length, "Length is not correct");
    }

    @Test
    void testGetAllGreaterCreatedDate() {
        ShowGroup[] response = restTemplate.getForObject(uri + "/groups?createdDate=>2006-10-12", ShowGroup[].class);
        assertNotNull(response, "Response is null");
        assertEquals(1, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithIncorrectCreatedDate() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () ->restTemplate.getForObject(uri + "/groups?createdDate=a", ShowGroup[].class)))
                .assertMsg("The filter is not valid and it should have a parameter filter (< - > - = - <= - =< - >= - => - == - != - <> - ><) and a date with the format YYYY-MM-DD.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/groups");
    }





}
