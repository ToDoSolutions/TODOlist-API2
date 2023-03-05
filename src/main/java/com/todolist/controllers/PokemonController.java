package com.todolist.controllers;

import com.todolist.dtos.ShowTask;
import com.todolist.dtos.Status;
import com.todolist.entity.Task;
import com.todolist.exceptions.BadRequestException;
import com.todolist.filters.NumberFilter;
import com.todolist.services.PokemonService;
import com.todolist.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1")
public class PokemonController {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator(); // Arreglar algún día.

    private final TaskService taskService;

    private final PokemonService pokemonService;


    @Autowired
    public PokemonController(TaskService taskService, PokemonService pokemonService) {
        this.taskService = taskService;
        this.pokemonService = pokemonService;
    }

    /* POKEMON OPERATIONS */

    @GetMapping("/pokemons") // GetAll
    public List<Map<String, Object>> getAllPokemon(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "The offset must be positive.") Integer offset,
            @RequestParam(defaultValue = "1154") @Min(value = 0, message = "The limit must be positive.") Integer limit,
            @RequestParam(required = false) NumberFilter hp,
            @RequestParam(required = false) NumberFilter attack,
            @RequestParam(required = false) NumberFilter defense,
            @RequestParam(required = false) NumberFilter specialAttack,
            @RequestParam(required = false) NumberFilter specialDefense,
            @RequestParam(required = false) NumberFilter speed,
            @RequestParam(defaultValue = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration") String fields) {
        return pokemonService.findAllPokemon(limit, offset).stream()
                .filter(pokemon -> {
                    var hp2 = pokemon.getStats().get(0).getBaseStat();
                    var attack2 = pokemon.getStats().get(1).getBaseStat();
                    var defense2 = pokemon.getStats().get(2).getBaseStat();
                    var specialAttack2 = pokemon.getStats().get(3).getBaseStat();
                    var specialDefense2 = pokemon.getStats().get(4).getBaseStat();
                    var speed2 = pokemon.getStats().get(5).getBaseStat();
                    return (hp == null || hp.isValid(Long.valueOf(hp2))) &&
                            (attack == null || attack.isValid(Long.valueOf(attack2))) &&
                            (defense == null || defense.isValid(Long.valueOf(defense2))) &&
                            (specialAttack == null || specialAttack.isValid(Long.valueOf(specialAttack2))) &&
                            (specialDefense == null || specialDefense.isValid(Long.valueOf(specialDefense2))) &&
                            (speed == null || speed.isValid(Long.valueOf(speed2)));
                }).map(pokemon -> new ShowTask(pokemonService.parsePokemon(null, null, null, null, 0, pokemon)).getFields(fields)).toList();
    }

    @GetMapping("/pokemon/{name}") // GetSoloTest
    public Map<String, Object> getPokemon(@PathVariable String name,
                                          @RequestParam(required = false) Status status,
                                          @RequestParam(required = false) LocalDate finishedDate,
                                          @RequestParam(required = false) LocalDate startDate,
                                          @RequestParam(required = false) Long priority,
                                          @RequestParam(defaultValue = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration") String fieldsTask,
                                          @RequestParam(required = false) @Min(value = 0, message = "The days must be positive.") Integer days) {
        Task task = pokemonService.findPokemonByName(name, status, finishedDate, startDate, priority, days);
        List<String> listFields = List.of(ShowTask.ALL_ATTRIBUTES.toLowerCase().split(","));
        if (!(Arrays.stream(fieldsTask.split(",")).allMatch(field -> listFields.contains(field.toLowerCase()))))
            throw new BadRequestException("The fields are invalid.");
        Set<ConstraintViolation<Task>> errors = validator.validate(task);
        if (!errors.isEmpty())
            throw new ConstraintViolationException(errors);
        return new ShowTask(task).getFields(fieldsTask);
    }

    @PostMapping("/pokemon/{name}") // PostTest
    public Map<String, Object> addPokemon(@PathVariable String name,
                                          @RequestParam(required = false) Status status,
                                          @RequestParam(required = false) LocalDate finishedDate,
                                          @RequestParam(required = false) LocalDate startDate,
                                          @RequestParam(required = false) Long priority,
                                          @RequestParam(required = false) @Min(value = 0, message = "The days must be positive.") Integer days) {
        Task task = pokemonService.findPokemonByName(name, status, finishedDate, startDate, priority, days);
        Set<ConstraintViolation<Task>> errors = validator.validate(task);
        if (!errors.isEmpty())
            throw new ConstraintViolationException(errors);
        return new ShowTask(taskService.saveTask(task)).getFields(ShowTask.ALL_ATTRIBUTES);
    }
}
