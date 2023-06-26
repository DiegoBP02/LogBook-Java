package com.dev.logBook.controller;

import com.dev.logBook.dtos.WorkoutDto;
import com.dev.logBook.entities.Exercise;
import com.dev.logBook.entities.Workout;
import com.dev.logBook.services.WorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/workouts")
public class WorkoutController {

    @Autowired
    private WorkoutService workoutService;

    @PostMapping
    public ResponseEntity<Workout> create(@Valid @RequestBody WorkoutDto workoutDTO) {
        Workout workout = workoutService.create(workoutDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(workout.getId()).toUri();

        return ResponseEntity.created(uri).body(workout);
    }

    @GetMapping
    public ResponseEntity<List<Workout>> findAll() {
        List<Workout> workouts = workoutService.findAll();
        return ResponseEntity.ok().body(workouts);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Workout> findById(@PathVariable UUID id) {
        Workout workout = workoutService.findById(id);
        return ResponseEntity.ok().body(workout);
    }

    @GetMapping(value = "/date/{date}")
    public ResponseEntity<Workout> findByDate(@PathVariable
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                              LocalDate date) {
        Workout workout = workoutService.findByDateAndUserId(date);
        return ResponseEntity.ok().body(workout);
    }

    @GetMapping(value = "/exercisesOutsideRepRange/{id}")
    public ResponseEntity<List<Exercise>> getExercisesOutsideRepsRange
            (@PathVariable UUID id) {
        List<Exercise> exercises = workoutService.getExercisesOutsideRepsRange(id);
        return ResponseEntity.ok().body(exercises);
    }

    @GetMapping(value = "/volumeLoad/{id}")
    public ResponseEntity<HashMap<String, Integer>> getVolumeLoad(@PathVariable UUID id) {
        HashMap<String, Integer> result = workoutService.calculateVolumeLoad(id);
        return ResponseEntity.ok().body(result);
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<Workout> update(@PathVariable UUID id,
                                          @Valid @RequestBody WorkoutDto workoutDto) {
        Workout workout = workoutService.update(id, workoutDto);
        return ResponseEntity.ok().body(workout);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Workout> delete(@PathVariable UUID id) {
        workoutService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
