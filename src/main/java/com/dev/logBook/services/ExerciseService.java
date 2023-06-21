package com.dev.logBook.services;

import com.dev.logBook.repositories.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExerciseService {

    @Autowired
    private WorkoutRepository workoutRepository;

}
