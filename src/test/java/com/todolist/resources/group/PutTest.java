package com.todolist.resources.group;

import com.todolist.entity.Group;
import com.todolist.model.ShowGroup;
import com.todolist.model.ShowTask;
import com.todolist.utilities.SQL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PutTest {

    Group group;
    ShowGroup showGroup;

    @BeforeEach
    void setUp() {
        SQL.start("jdbc:mariadb://localhost:3306/todolist-api2", "root", "mazetosan$root");
        SQL.read("data/create.sql");
        group = new Group();
        group.setName("Group 1");
        group.setDescription("Group 1 description");
        String uri = "http://localhost:8080/api/v1/groups";
        RestTemplate restTemplate = new RestTemplate();
        showGroup = restTemplate.postForObject(uri, group, ShowGroup.class);
    }

    // Correct
    @Test
    void testPostFine() {
        String uri1 = "http://localhost:8080/api/v1/groups";
        RestTemplate restTemplate = new RestTemplate();
        group.setName("Group 2");
        group.setDescription("Group 2 description");
        group.setIdGroup(showGroup.getIdGroup());
        ShowGroup response = restTemplate.exchange(uri1, HttpMethod.PUT, new HttpEntity<>(group), ShowGroup.class).getBody();
        System.out.println(response);
        assertEquals(1, response.getIdGroup(), "IdGroup is not correct");
        String uri2 = "http://localhost:8080/api/v1/groups/1";
        response = restTemplate.getForObject(uri2, ShowGroup.class);
        assertEquals(1, response.getIdGroup(), "IdGroup is not correct");
    }

    // Name
    @Test
    void testPostWithNullOrEmptyName() {
        group.setName(null);
        String uri = "http://localhost:8080/api/v1/groups";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(group), ShowGroup.class));
        group.setName("");
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(group), ShowGroup.class));
    }

    @Test
    void testPostWithNameGreaterThan50() {
        group.setName(new String(new char[51]).replace("\0", "a"));
        String uri = "http://localhost:8080/api/v1/groups";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(group), ShowGroup.class));
    }

    // Description
    @Test
    void testPostWithNullOrEmptyDescription() {
        group.setDescription(null);
        String uri = "http://localhost:8080/api/v1/groups";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(group), ShowGroup.class));
        group.setDescription("");
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(group), ShowGroup.class));
    }

    @Test
    void testPostWithDescriptionGreaterThan500() {
        group.setDescription(new String(new char[501]).replace("\0", "a"));
        String uri = "http://localhost:8080/api/v1/groups";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(group), ShowGroup.class));
    }

    // CreatedDate
    @Test
    void testPostWithInvalidCreatedDate() {
        group.setCreatedDate("ayer");
        String uri = "http://localhost:8080/api/v1/groups";
        RestTemplate restTemplate = new RestTemplate();
        assertThrows(HttpClientErrorException.class, () -> restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(group), ShowGroup.class));
    }
}