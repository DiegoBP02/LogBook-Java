package com.dev.logBook.controller;

import com.dev.logBook.dtos.WorkoutDto;
import com.dev.logBook.entities.Workout;
import com.dev.logBook.services.WorkoutService;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Controller(value = "/workout")
public class WorkoutController {

    @Autowired
    private WorkoutService workoutService;

    @PostMapping
    public ResponseEntity<Workout> create(@Valid @RequestBody WorkoutDto workoutDTO){}

    @GetMapping
    public ResponseEntity<List<Workout>> findAll(){}

    @GetMapping(value = "/{id}")
    public ResponseEntity<Workout> findById(@PathVariable UUID id){}

    @PatchMapping(value = "/{id}")
    public ResponseEntity<Workout> update(@PathVariable UUID id, @Valid @RequestBody WorkoutDto workoutDto){}

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Workout> delete(@PathVariable UUID id){}

}
