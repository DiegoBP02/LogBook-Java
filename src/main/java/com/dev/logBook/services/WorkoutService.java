package com.dev.logBook.services;

import com.dev.logBook.dtos.WorkoutDto;
import com.dev.logBook.entities.User;
import com.dev.logBook.entities.Workout;
import com.dev.logBook.repositories.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class WorkoutService {

    @Autowired
    private WorkoutRepository workoutRepository;

}
