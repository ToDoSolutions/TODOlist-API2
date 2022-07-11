package com.todolist.resources.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

class GetAllTest {

    @BeforeEach
    void setUp() {
        SQL.start("jdbc:mariadb://localhost:3306/todolist-api2", "root", "mazetosan$root");
        SQL.read("data/V2__populate_db.sql");
    }

    @Test
    void testGetAllFine() {
        String uri = "http://localhost:8080/api/v1/users";
        RestTemplate restTemplate = new RestTemplate();
    }


}
