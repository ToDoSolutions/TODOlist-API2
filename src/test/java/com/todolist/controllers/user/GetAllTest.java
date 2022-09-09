package com.todolist.controllers.user;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.TODOlistApplication;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import com.todolist.exceptions.ManagerException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testGetAllFine() {
        ShowUser[] response = restTemplate.getForObject(uri + "/users", ShowUser[].class);
        RestTemplate restTemplate = new RestTemplate();
        assertNotNull(response, "Response is null");
        assertEquals(6, response.length, "Length is not correct");
    }

    // Offset
    @Test
    void testGetAllWithCorrectOffset() {
        ShowUser[] response = restTemplate.getForObject(uri + "/users?offset=2", ShowUser[].class);
        assertNotNull(response, "Response is null");
        assertEquals(5, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithIncorrectOffset() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/users?offset=a", ShowTask[].class)))
                .assertMsg("Error while parsing the next string  \\a\\.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/users");
    }

    @Test
    void testGetAllWithNegativeOffset() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/users?offset=-1", ShowTask[].class)))
                .assertMsg("The offset must be positive.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/users");
    }

    // Limit
    @Test
    void testGetAllWithCorrectLimit() {
        ShowUser[] response = restTemplate.getForObject(uri + "/users?limit=2", ShowUser[].class);
        assertNotNull(response, "Response is null");
        assertEquals(2, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithIncorrectLimit() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/users?limit=a", ShowUser[].class)))
                .assertMsg("Error while parsing the next string  \\a\\.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/users");
    }

    @Test
    void testGetAllWithNegativeLimit() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/users?limit=-2", ShowUser[].class)))
                .assertMsg("The limit must be positive.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/users");
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
        ShowUser[] response = restTemplate.getForObject(uri + "/tasks?order=title", ShowUser[].class);
        assertNotNull(response, "Response is null");
        assertEquals(8, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithIncorrectOrder() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/users?order=a", ShowUser[].class)))
                .assertMsg("The order is invalid.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/users");
    }

    @Test
    void testGetAllWithOrderAndOffset() {
        ShowUser[] response = restTemplate.getForObject(uri + "/tasks?order=title&offset=2", ShowUser[].class);
        assertNotNull(response, "Response is null");
        assertEquals(7, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithOrderAndLimit() {
        ShowTask[] response = restTemplate.getForObject(uri + "/tasks?order=title&limit=2", ShowTask[].class);
        assertNotNull(response, "Response is null");
        assertEquals(2, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithOrderAndOffsetAndLimit() {
        ShowUser[] response = restTemplate.getForObject(uri + "/users?order=name&offset=2&limit=2", ShowUser[].class);
        assertNotNull(response, "Response is null");
        assertEquals(2, response.length, "Length is not correct");
    }

    // Task fields
    @Test
    void testGetAllWithCorrectFields() {
        ShowTask[] response = restTemplate.getForObject(uri + "/users?fieldsTask=title,description", ShowTask[].class);
        assertNotNull(response, "Response is null");
        assertEquals(6, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithIncorrectFields() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () ->restTemplate.getForObject(uri + "/users?fieldsTask=a", ShowTask[].class)))
                .assertMsg("The tasks' fields are invalid.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/users");
    }

    // User Fields
    @Test
    void testGetAllWithUserCorrectFields() {
        ShowUser[] response = restTemplate.getForObject(uri + "/users?fieldsUser=name,surname", ShowUser[].class);
        assertNotNull(response, "Response is null");
        assertEquals(6, response.length, "Length is not correct");
    }

    @Test
    void testGetAllWithIncorrectUserFields() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () ->restTemplate.getForObject(uri + "/users?fieldsUser=a", ShowUser[].class)))
                .assertMsg("The users' fields are invalid.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/users");
    }

    // Name
    @Test
    void testGetAllByName() {
        ShowUser[] response = restTemplate.getForObject(uri + "/users?name=Misco", ShowUser[].class);
        assertNotNull(response, "Response is null");
        assertEquals(1, response.length, "Length is not correct");
    }

    // Surname
    @Test
    void testGetAllBySurname() {
        ShowUser[] response = restTemplate.getForObject(uri + "/users?surname=Jones", ShowUser[].class);
        assertNotNull(response, "Response is null");
        assertEquals(1, response.length, "Length is not correct");
    }

    // Email
    @Test
    void testGetAllByEmail() {
        ShowUser[] response = restTemplate.getForObject(uri + "/users?email=niunpelotonto@tortilla.ong", ShowUser[].class);
        assertNotNull(response, "Response is null");
        assertEquals(1, response.length, "Length is not correct");
    }

    @Test
    void testGetAllByIncorrectEmail() {
       ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/users?email=Pipo", ShowUser[].class)))
               .assertMsg("The email is invalid.")
               .assertStatus("Bad Request")
               .assertPath("/api/v1/users");

    }

    // Avatar
    @Test
    void testGetAllByAvatar() {
        ShowUser[] response = restTemplate.getForObject(uri + "/users?avatar=http://pm1.narvii.com/6120/9cd70762280f430ded8158c06c287e82b84d0101_00.jpg", ShowUser[].class);
        assertNotNull(response, "Response is null");
        assertEquals(1, response.length, "Length is not correct");
    }

    @Test
    void testGetAllByIncorrectAvatar() {
        ManagerException.of(assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/users?avatar=Pipo", ShowUser[].class)))
                .assertMsg("The avatar is invalid.")
                .assertStatus("Bad Request")
                .assertPath("/api/v1/users");
    }

    // Bio
    @Test
    void testGetAllByBio() {
        ShowUser[] response = restTemplate.getForObject(uri + "/users?bio=Solamente defender al mundo del caos", ShowUser[].class);
        assertNotNull(response, "Response is null");
        assertEquals(1, response.length, "Length is not correct");
    }

    // Location
    @Test
    void testGetAllByLocation() {
        ShowUser[] response = restTemplate.getForObject(uri + "/users?location=La Tierra", ShowUser[].class);
        assertNotNull(response, "Response is null");
        assertEquals(1, response.length, "Length is not correct");
    }

    // Task completed
    @Test
    void testGetAllEqualTaskCompleted() {
        ShowUser[] response = restTemplate.getForObject(uri + "/users?taskCompleted==1", ShowUser[].class);
        assertNotNull(response, "Response is null");
        assertEquals(1, response.length, "Length is not correct");
    }

    @Test
    void testGetAllGreaterTaskCompleted() {
        ShowUser[] response = restTemplate.getForObject(uri + "/users?taskCompleted=>1", ShowUser[].class);
        assertNotNull(response, "Response is null");
        assertEquals(0, response.length, "Length is not correct");
    }

    @Test
    void testGetAllLowerTaskCompleted() {
        ShowUser[] response = restTemplate.getForObject(uri + "/users?taskCompleted=<1", ShowUser[].class);
        assertNotNull(response, "Response is null");
        assertEquals(5, response.length, "Length is not correct");
    }


}
