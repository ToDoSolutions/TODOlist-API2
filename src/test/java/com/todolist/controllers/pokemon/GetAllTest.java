package com.todolist.controllers.pokemon;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.todolist.TODOlistApplication;
import com.todolist.dtos.ShowTask;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@FlywayTest(additionalLocations = "db/migration", value = @DataSource(url = "jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
// @FlywayTest(additionalLocations = "db/migration", value = @DataSource(url = "jdbc:mysql://uqiweqtspt5rb4xp:uWHt8scUWIMHRDzt7HCg@b8iyr7xai8wk75ismpbt-mysql.services.clever-cloud.com:3306/b8iyr7xai8wk75ismpbt", username = "uqiweqtspt5rb4xp", password = "uWHt8scUWIMHRDzt7HCg"))
public class GetAllTest {

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
        ShowTask[] response = restTemplate.getForObject(uri + "/pokemons?limit=10", ShowTask[].class);
        assertEquals(10, response.length, "The number of pokemons is not 50");
    }

    // hp
    @Test
    void testGetAllByHp() {
        ShowTask[] response = restTemplate.getForObject(uri + "/pokemons?hp==35&limit=25", ShowTask[].class);
        assertTrue(Arrays.stream(response).anyMatch(pokemon -> pokemon.getTitle().contains("pikachu")), "The number of pokemons is not 50");
    }

    // attack
    @Test
    void testGetAllByAttack() {
        ShowTask[] response = restTemplate.getForObject(uri + "/pokemons?attack==55&limit=25", ShowTask[].class);
        assertTrue(Arrays.stream(response).anyMatch(pokemon -> pokemon.getTitle().contains("pikachu")), "The number of pokemons is not 50");
    }

    // defense
    @Test
    void testGetAllByDefense() {
        ShowTask[] response = restTemplate.getForObject(uri + "/pokemons?defense==40&limit=25", ShowTask[].class);
        assertTrue(Arrays.stream(response).anyMatch(pokemon -> pokemon.getTitle().contains("pikachu")), "The number of pokemons is not 50");
    }

    // Special attack
    @Test
    void testGetAllBySpAttack() {
        ShowTask[] response = restTemplate.getForObject(uri + "/pokemons?specialAttack==50&limit=25", ShowTask[].class);
        assertTrue(Arrays.stream(response).anyMatch(pokemon -> pokemon.getTitle().contains("pikachu")), "The number of pokemons is not 50");
    }

    // Special defense
    @Test
    void testGetAllBySpDefense() {
        ShowTask[] response = restTemplate.getForObject(uri + "/pokemons?specialDefense==50&limit=25", ShowTask[].class);
        assertTrue(Arrays.stream(response).anyMatch(pokemon -> pokemon.getTitle().contains("pikachu")), "The number of pokemons is not 50");
    }

    // Speed
    @Test
    void testGetAllBySpeed() {
        ShowTask[] response = restTemplate.getForObject(uri + "/pokemons?speed==90&limit=25", ShowTask[].class);
        assertTrue(Arrays.stream(response).anyMatch(pokemon -> pokemon.getTitle().contains("pikachu")), "The number of pokemons is not 50");
    }

    // Fields
    @Test
    void testGetAllByFields() {
        ShowTask[] response = restTemplate.getForObject(uri + "/pokemons?limit=10&fields=title,description", ShowTask[].class);
        assertEquals(10, response.length, "The number of pokemons is not 50");
    }
}
