package com.todolist.controllers.github.repo;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.TODOlistApplication;
import com.todolist.dtos.ShowUser;
import com.todolist.entity.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

@FlywayTest(additionalLocations = "db/migration", value = @DataSource(url = "jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
// @FlywayTest(additionalLocations = "db/migration", value = @DataSource(url = "jdbc:mysql://uqiweqtspt5rb4xp:uWHt8scUWIMHRDzt7HCg@b8iyr7xai8wk75ismpbt-mysql.services.clever-cloud.com:3306/b8iyr7xai8wk75ismpbt", username = "uqiweqtspt5rb4xp", password = "uWHt8scUWIMHRDzt7HCg"))
public class DeleteTest {

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
        User user = new User();
        user.setUsername("Mazetosan");
        user.setPassword("1234");
        user.setToken("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");
        ShowUser showUser = restTemplate.postForObject(uri + "/user", user, ShowUser.class);
        showUser = restTemplate.postForObject(uri + "github/user", user, ShowUser.class);
        showUser = restTemplate.postForObject(uri + "/user/" + showUser.getIdUser() + "/task/1", null, ShowUser.class);
        // restTemplate.postForObject(uri + "/github/");
    }
}
