package com.dev.logBook.controller;

import com.dev.logBook.dtos.ExerciseDto;
import com.dev.logBook.entities.Exercise;
import com.dev.logBook.services.ExerciseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/exercises")
public class ExerciseController {

    @Autowired
    private ExerciseService exerciseService;

    @PostMapping
    public ResponseEntity<Exercise> create(@Valid @RequestBody ExerciseDto exerciseDto) {
        Exercise exercise = exerciseService.create(exerciseDto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(exercise.getId()).toUri();

        return ResponseEntity.created(uri).body(exercise);
    }

    @GetMapping
    public ResponseEntity<List<Exercise>> findAll() {
        List<Exercise> exercises = exerciseService.findAll();
        return ResponseEntity.ok().body(exercises);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Exercise> findById(@PathVariable UUID id) {
        Exercise exercise = exerciseService.findById(id);
        return ResponseEntity.ok().body(exercise);
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<Exercise> update(@PathVariable UUID id,
                                           @Valid @RequestBody ExerciseDto exerciseDto) {
        Exercise exercise = exerciseService.update(id, exerciseDto);
        return ResponseEntity.ok().body(exercise);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Exercise> delete(@PathVariable UUID id) {
        exerciseService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
