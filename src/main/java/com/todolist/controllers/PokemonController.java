package com.todolist.controllers;

import com.todolist.dtos.ShowTask;
import com.todolist.filters.NumberFilter;
import com.todolist.services.PokemonService;
import com.todolist.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tasks/pokemon")
public class PokemonController {



    @Autowired
    private TaskService taskService;

    @Autowired
    private PokemonService pokemonService;



    @GetMapping
    public List<ShowTask> getAllPokemon(@RequestParam(required = false) NumberFilter hp,
                                        @RequestParam(required = false) NumberFilter attack,
                                        @RequestParam(required = false) NumberFilter defense,
                                        @RequestParam(required = false) NumberFilter specialAttack,
                                        @RequestParam(required = false) NumberFilter specialDefense,
                                        @RequestParam(required = false) NumberFilter speed) {
        return  pokemonService.findAllPokemon().stream()
                .filter(pokemon -> {
                    var hp2 = pokemon.getStats().get(0).getBaseStat();
                    var attack2 = pokemon.getStats().get(1).getBaseStat();
                    var defense2 = pokemon.getStats().get(2).getBaseStat();
                    var specialAttack2 = pokemon.getStats().get(3).getBaseStat();
                    var specialDefense2 = pokemon.getStats().get(4).getBaseStat();
                    var speed2 = pokemon.getStats().get(5).getBaseStat();
                    return hp.isValid(Long.valueOf(hp2)) && attack.isValid(Long.valueOf(attack2)) && defense.isValid(Long.valueOf(defense2)) && specialAttack.isValid(Long.valueOf(specialAttack2)) && specialDefense.isValid(Long.valueOf(specialDefense2)) && speed.isValid(Long.valueOf(speed2));
                }).map(pokemon -> new ShowTask(pokemonService.parsePokemon(null, null, null, null, 0, pokemon))).toList();
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
        return new ShowTask(pokemonService.findPokemonByName(name, status, finishedDate, startDate, priority, days)).getFields(fields);
    }

    @PostMapping("/{name}")
    public Map<String, Object> addPokemon(@PathVariable String name,
                                          @RequestParam(required = false) @Pattern(regexp = "DRAFT|IN_PROGRESS|DONE|IN_REVISION|CANCELLED", message = "The status is invalid.") String status,
                                          @RequestParam(required = false) @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The finishedDate is invalid.") String finishedDate,
                                          @RequestParam(required = false) @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The startDate is invalid.") String startDate,
                                          @RequestParam(required = false) @Max(value = 5, message = "The priority must be between 0 and 5") Long priority,
                                          @RequestParam(defaultValue = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration") String fields,
                                          @RequestParam(required = false) Integer days) {
        return new ShowTask(taskService.saveTask(pokemonService.findPokemonByName(name, status, finishedDate, startDate, priority, days))).getFields(fields);
    }


}
