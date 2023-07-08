package com.dev.logBook.controller;

import com.dev.logBook.dtos.ExerciseDto;
import com.dev.logBook.entities.Exercise;
import com.dev.logBook.enums.Muscles;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/muscles")
public class MusclesController {

    @GetMapping
    public ResponseEntity<Muscles[]> findAll() {
        return ResponseEntity.ok().body(Muscles.values());
    }
}
