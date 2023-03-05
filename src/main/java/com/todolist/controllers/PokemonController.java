package com.todolist.controllers;

import com.todolist.component.DTOManager;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.Status;
import com.todolist.entity.Task;
import com.todolist.filters.NumberFilter;
import com.todolist.services.PokemonService;
import com.todolist.services.TaskService;
import com.todolist.utilities.Predicator;
import com.todolist.validators.FieldValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1")
public class PokemonController {

    private final Validator validator;
    private final TaskService taskService;

    private final PokemonService pokemonService;
    private final DTOManager dtoManager;
    private final FieldValidator fieldValidator;


    @Autowired
    public PokemonController(Validator validator, TaskService taskService, PokemonService pokemonService, DTOManager dtoManager, FieldValidator fieldValidator) {
        this.validator = validator;
        this.taskService = taskService;
        this.pokemonService = pokemonService;
        this.dtoManager = dtoManager;
        this.fieldValidator = fieldValidator;
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
            @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES_STRING) String fields) {
        return pokemonService.findAllPokemon(limit, offset).stream()
                .filter(pokemon -> {
                    var hp2 = pokemon.getStats().get(0).getBaseStat();
                    var attack2 = pokemon.getStats().get(1).getBaseStat();
                    var defense2 = pokemon.getStats().get(2).getBaseStat();
                    var specialAttack2 = pokemon.getStats().get(3).getBaseStat();
                    var specialDefense2 = pokemon.getStats().get(4).getBaseStat();
                    var speed2 = pokemon.getStats().get(5).getBaseStat();
                    return Predicator.isNullOrValid(hp, h -> h.isValid(Long.valueOf(hp2))) &&
                            Predicator.isNullOrValid(attack, a -> a.isValid(Long.valueOf(attack2))) &&
                            Predicator.isNullOrValid(defense, d -> d.isValid(Long.valueOf(defense2))) &&
                            Predicator.isNullOrValid(specialAttack, s -> s.isValid(Long.valueOf(specialAttack2))) &&
                            Predicator.isNullOrValid(specialDefense, s -> s.isValid(Long.valueOf(specialDefense2))) &&
                            Predicator.isNullOrValid(speed, s -> speed.isValid(Long.valueOf(speed2)));
                }).map(pokemon -> {
                    Task task = pokemonService.parsePokemon(pokemon);
                    return dtoManager.getShowTaskAsJson(task, fields);
                }).toList();
    }

    @GetMapping("/pokemon/{name}") // GetSoloTest
    public Map<String, Object> getPokemon(@PathVariable String name,
                                          @RequestParam(required = false) Status status,
                                          @RequestParam(required = false) LocalDate finishedDate,
                                          @RequestParam(required = false) LocalDate startDate,
                                          @RequestParam(required = false) Long priority,
                                          @RequestParam(defaultValue = ShowTask.ALL_ATTRIBUTES_STRING) String fieldsTask,
                                          @RequestParam(required = false) @Min(value = 0, message = "The days must be positive.") Integer days) {
        Task task = pokemonService.findPokemonByName(name, status, finishedDate, startDate, priority, days);
        fieldValidator.taskFieldValidate(fieldsTask);
        Set<ConstraintViolation<Task>> errors = validator.validate(task);
        if (!errors.isEmpty())
            throw new ConstraintViolationException(errors);
        return dtoManager.getShowTaskAsJson(task, fieldsTask);
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
        Task taskSaved = taskService.saveTask(task);
        return dtoManager.getShowTaskAsJson(taskSaved);
    }
}
