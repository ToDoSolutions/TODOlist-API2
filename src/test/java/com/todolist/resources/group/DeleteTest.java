package com.todolist.resources.group;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.dtos.ShowGroup;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@FlywayTest(additionalLocations = "db/testWithData", value = @DataSource(url ="jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
class DeleteTest {

    @Test
    void testDeleteFine() {
        String uri = "http://localhost:8080/api/v1/groups/1";
        RestTemplate restTemplate = new RestTemplate();
        ShowGroup response = restTemplate.exchange(uri, HttpMethod.DELETE, null, ShowGroup.class).getBody();
        assertEquals(1, response.getIdGroup(), "IdGroup is not correct");
        Throwable exception = assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(uri, ShowGroup.class));
        assertEquals("404", exception.getMessage().split(":")[0].trim(), "Status code is not correct");
        assertEquals("The group with idGroup 1 does not exist.", exception.getMessage().split(":")[6].split(",")[0].replace("\"", "").trim(), "Message is not correct");
    }

    @Test
    void testDeleteWithWrongId() {
        String uri = "http://localhost:8080/api/v1/groups/0";
        RestTemplate restTemplate = new RestTemplate();
        Throwable exception = assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.DELETE, null, ShowGroup.class));
        assertEquals("404", exception.getMessage().split(":")[0].trim(), "Status code is not correct");
        assertEquals("The group with idGroup 0 does not exist.", exception.getMessage().split(":")[6].split(",")[0].replace("\"", "").trim(), "Message is not correct");
    }
}
