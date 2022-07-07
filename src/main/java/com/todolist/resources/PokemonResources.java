package com.todolist.resources;

import com.todolist.dtos.Difficulty;
import com.todolist.dtos.ShowTask;
import com.todolist.entity.Task;
import com.todolist.entity.pokemon.Pokemon;
import com.todolist.entity.pokemon.Stat;
import com.todolist.parsers.TaskParser;
import com.todolist.repository.Repositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tasks/pokemon")
public class PokemonResources {

    @Autowired
    @Qualifier("repositories")
    private Repositories repositories;

    @Autowired
    @Qualifier("taskParser")
    private TaskParser taskParser;

    @GetMapping("/{name}")
    public Map<String, Object> getPokemon(@PathVariable String name,
                                          @RequestParam(required = false) @Pattern(regexp = "DRAFT|IN_PROGRESS|DONE|IN_REVISION|CANCELLED", message = "The status is invalid.") String status,
                                          @RequestParam(required = false) @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The finishedDate is invalid.") String finishedDate,
                                          @RequestParam(required = false) @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The startDate is invalid.") String startDate,
                                          @RequestParam(required = false) @Max(value = 5, message="The priority must be between 0 and 5") Integer priority,
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
                                          @RequestParam(required = false) @Max(value = 5, message="The priority must be between 0 and 5") Integer priority,
                                          @RequestParam(defaultValue = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration") String fields,
                                          @RequestParam(required = false) Integer days) {
        return new ShowTask(repositories.taskRepository.save(parsePokemon(name, status, finishedDate, startDate, priority, days))).getFields(fields);
    }

    public static Task parsePokemon(String name, String status, String finishedDate, String startDate, Integer priority, Integer days) {
        String uri = "https://pokeapi.co/api/v2/pokemon/" + name;
        RestTemplate restTemplate = new RestTemplate();
        Pokemon response = restTemplate.getForObject(uri, Pokemon.class);
        StringBuilder types = new StringBuilder();
        if (finishedDate != null && days != null)
            throw new IllegalArgumentException("You can't set both finishedDate and days");
        else if (finishedDate == null && days == null)
            throw new IllegalArgumentException("You must set either finishedDate or days");
        for (int i = 0; i < response.getTypes().size(); i++) {
            types.append(response.getTypes().get(i).getType().getName());
            if (i != response.getTypes().size() - 1)
                types.append(" - ");
        }
        Task task = new Task();
        task.setTitle("Catch: " + response.getName());
        task.setDescription("Type pokemon: " + types);
        task.setStatus(status);
        task.setFinishedDate(finishedDate == null ?
                startDate == null ?
                        LocalDate.now().plusDays(days).toString() :
                        LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE).plusDays(days).toString() :
                finishedDate);
        task.setStartDate(startDate == null ? LocalDate.now().toString() : startDate);
        task.setPriority(priority);
        task.setAnnotation(getPokemonAnnotation(response));
        task.setDifficulty(getPokemonDifficulty(response).toString());
        task.setIdTask(-1);
        return task;
    }



    private static String getPokemonAnnotation(Pokemon pokemon) {
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

    private static Difficulty getPokemonDifficulty(Pokemon pokemon) {
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

    private static Double getAvgStats(Pokemon pokemon) {
        return pokemon.getStats().stream().mapToInt(Stat::getBaseStat).average().orElse(0);
    }
}
