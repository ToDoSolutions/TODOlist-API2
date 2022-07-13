package com.todolist.controllers.group;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.dtos.ShowGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// @FlywayTest(additionalLocations = "db/testWithOutData", value = @DataSource(url = "jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
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
        ShowGroup response = restTemplate.exchange(uri + "/groups/1", HttpMethod.DELETE, null, ShowGroup.class).getBody();
        assertEquals(1, response.getIdGroup(), "IdGroup is not correct");
        Throwable exception = assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri + "/groups/1", ShowGroup.class));
        assertEquals("404", exception.getMessage().split(":")[0].trim(), "Status code is not correct");
        assertEquals("The group with idGroup 1 does not exist.", exception.getMessage().split(":")[6].split(",")[0].replace("\"", "").trim(), "Message is not correct");
    }

    @Test
    void testDeleteWithWrongId() {
        Throwable exception = assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri + "/groups/0", HttpMethod.DELETE, null, ShowGroup.class));
        assertEquals("404", exception.getMessage().split(":")[0].trim(), "Status code is not correct");
        assertEquals("The group with idGroup 0 does not exist.", exception.getMessage().split(":")[6].split(",")[0].replace("\"", "").trim(), "Message is not correct");
    }
}
