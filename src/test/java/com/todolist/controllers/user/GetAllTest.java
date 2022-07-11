package com.todolist.controllers.user;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

@FlywayTest(additionalLocations = "db/testWithData", value = @DataSource(url ="jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
class GetAllTest {

    @Test
    void testGetAllFine() {
        String uri = "http://localhost:8080/api/v1/users";
        RestTemplate restTemplate = new RestTemplate();
    }


}
