package com.todolist.services;

import com.todolist.dtos.Difficulty;
import com.todolist.dtos.Status;
import com.todolist.entity.Task;
import com.todolist.entity.pokemon.AllPokemon;
import com.todolist.entity.pokemon.Pokemon;
import com.todolist.entity.pokemon.Stat;
import com.todolist.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
public class PokemonService {

    @Value("${pokemon.api.url}")
    private String startUrl;

    public Task findPokemonByName(String name, Status status, LocalDate finishedDate, LocalDate startDate, Long priority, Integer days) {
        String url = startUrl + "/pokemon/" + name;
        RestTemplate restTemplate = new RestTemplate();
        Pokemon response = restTemplate.getForObject(url, Pokemon.class);
        return parsePokemon(status, finishedDate, startDate, priority, days, response);
    }

    public Task parsePokemon(Status status, LocalDate finishedDate, LocalDate startDate, Long priority, Integer days, Pokemon pokemon) {
        StringBuilder types = new StringBuilder();
        if (finishedDate != null && days != null)
            throw new BadRequestException("You can't use both finishedDate and days");
        if (finishedDate == null && days == null)
            throw new BadRequestException("You must set either finishedDate or days");
        for (int i = 0; i < pokemon.getTypes().size(); i++) {
            types.append(pokemon.getTypes().get(i).getType().getName());
            if (i != pokemon.getTypes().size() - 1)
                types.append(" - ");
        }
        return Task.of("Catch: " + pokemon.getName(), "Type pokemon: " + types, getPokemonAnnotation(pokemon), status,
                finishedDate == null ?
                        startDate == null ?
                                LocalDate.now().plusDays(days):
                                startDate.plusDays(days):
                        finishedDate,
                startDate == null ? LocalDate.now() : startDate, priority,
                getPokemonDifficulty(pokemon));
    }

    private String getPokemonAnnotation(Pokemon pokemon) {
        if (getAvgStats(pokemon) < 50) {
            return "easy peasy lemon squeezy, take one pokeball";
        } else if (getAvgStats(pokemon) < 100) {
            return "mmmh, you will nead some pokeballs";
        } else if (getAvgStats(pokemon) < 150) {
            return "uffff, you must take a great a amount of superballs";
        } else if (getAvgStats(pokemon) < 200) {
            return "Yisus, if you do not catch dozens of super balls, you will not be able to catch it.";
        } else {
            return "LMFAO, take the entire Pokemon Center in your bag";
        }
    }

    private Difficulty getPokemonDifficulty(Pokemon pokemon) {
        if (getAvgStats(pokemon) < 50)
            return Difficulty.EASY;
        else if (getAvgStats(pokemon) < 100)
            return Difficulty.MEDIUM;
        else if (getAvgStats(pokemon) < 150)
            return Difficulty.HARD;
        else if (getAvgStats(pokemon) < 200)
            return Difficulty.HARDCORE;
        else
            return Difficulty.I_WANT_TO_DIE;
    }

    private Double getAvgStats(Pokemon pokemon) {
        return pokemon.getStats().stream().mapToInt(Stat::getBaseStat).average().orElse(0);
    }

    public List<Pokemon> findAllPokemon(Integer limit, Integer offset) {
        RestTemplate restTemplate = new RestTemplate();
        AllPokemon response = restTemplate.getForObject(startUrl + "/pokemon?limit=" + limit + "&offset=" + offset, AllPokemon.class);
        return response.getResults().stream()
                .map(url -> restTemplate.getForObject(url.getUrl(), Pokemon.class))
                .filter(Objects::nonNull)
                .toList();
    }
}
