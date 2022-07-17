package com.todolist.controllers.pokemon;

import com.google.common.base.Preconditions;
import com.todolist.dtos.Difficulty;
import com.todolist.dtos.ShowTask;
import com.todolist.entity.Task;
import com.todolist.entity.pokemon.Pokemon;
import com.todolist.entity.pokemon.Stat;
import com.todolist.services.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tasks/pokemon")
public class PokemonController {

    @Value("${pokemon.api.url}")
    private String startUrl;

    @Autowired
    private TaskService taskService;

    public Task parsePokemon(String name, String status, String finishedDate, String startDate, Long priority, Integer days) {
        String url = startUrl + "/pokemon/" + name;
        RestTemplate restTemplate = new RestTemplate();
        Pokemon response = restTemplate.getForObject(url, Pokemon.class);
        StringBuilder types = new StringBuilder();
        Preconditions.checkArgument(!(finishedDate != null && days != null), "You can't set both finishedDate and days");
        Preconditions.checkArgument(!(finishedDate == null && days == null), "You must set either finishedDate or days");
        for (int i = 0; i < response.getTypes().size(); i++) {
            types.append(response.getTypes().get(i).getType().getName());
            if (i != response.getTypes().size() - 1)
                types.append(" - ");
        }
        return Task.of("Catch: " + response.getName(), "Type pokemon: " + types, getPokemonAnnotation(response), status,
                finishedDate == null ?
                        startDate == null ?
                                LocalDate.now().plusDays(days).toString() :
                                LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE).plusDays(days).toString() :
                        finishedDate,
                startDate == null ? LocalDate.now().toString() : startDate, priority,
                getPokemonDifficulty(response).toString());
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

    @GetMapping("/{name}")
    public Map<String, Object> getPokemon(@PathVariable String name,
                                          @RequestParam(required = false) @Pattern(regexp = "DRAFT|IN_PROGRESS|DONE|IN_REVISION|CANCELLED", message = "The status is invalid.") String status,
                                          @RequestParam(required = false) @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The finishedDate is invalid.") String finishedDate,
                                          @RequestParam(required = false) @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The startDate is invalid.") String startDate,
                                          @RequestParam(required = false) @Max(value = 5, message = "The priority must be between 0 and 5") Long priority,
                                          @RequestParam(defaultValue = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration") String fields,
                                          @RequestParam(required = false) Integer days
    ) {
        return new ShowTask(parsePokemon(name, status, finishedDate, startDate, priority, days)).getFields(fields);
    }

    @PostMapping("/{name}")
    public Map<String, Object> addPokemon(@PathVariable String name,
                                          @RequestParam(required = false) @Pattern(regexp = "DRAFT|IN_PROGRESS|DONE|IN_REVISION|CANCELLED", message = "The status is invalid.") String status,
                                          @RequestParam(required = false) @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The finishedDate is invalid.") String finishedDate,
                                          @RequestParam(required = false) @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The startDate is invalid.") String startDate,
                                          @RequestParam(required = false) @Max(value = 5, message = "The priority must be between 0 and 5") Long priority,
                                          @RequestParam(defaultValue = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration") String fields,
                                          @RequestParam(required = false) Integer days) {
        return new ShowTask(taskService.saveTask(parsePokemon(name, status, finishedDate, startDate, priority, days))).getFields(fields);
    }
}
