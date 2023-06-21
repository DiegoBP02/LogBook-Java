package com.dev.logBook.controller;

import com.dev.logBook.dtos.ExerciseDto;
import com.dev.logBook.dtos.ExerciseDto;
import com.dev.logBook.entities.Exercise;
import com.dev.logBook.services.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Controller(value = "/workout")
public class ExerciseController {

    @Autowired
    private ExerciseService workoutService;

    @PostMapping
    public ResponseEntity<Exercise> create(@Valid @RequestBody ExerciseDto exerciseDto){}

    @GetMapping
    public ResponseEntity<List<Exercise>> findAll(){}

    @GetMapping(value = "/{id}")
    public ResponseEntity<Exercise> findById(@PathVariable UUID id){}

    @PatchMapping(value = "/{id}")
    public ResponseEntity<Exercise> update(@PathVariable UUID id, @Valid @RequestBody ExerciseDto exerciseDto){}

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Exercise> delete(@PathVariable UUID id){}

}
