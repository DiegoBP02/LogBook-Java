package com.dev.logBook.services;

import com.dev.logBook.dtos.ExerciseDto;
import com.dev.logBook.entities.Exercise;
import com.dev.logBook.entities.User;
import com.dev.logBook.entities.Workout;
import com.dev.logBook.repositories.ExerciseRepository;
import com.dev.logBook.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

import static com.dev.logBook.services.utils.CheckOwnership.checkOwnership;

@Service
public class ExerciseService {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private WorkoutService workoutService;

    public Exercise create(ExerciseDto exerciseDto) {
        User user = getCurrentUser();
        Workout workout = workoutService.findById(exerciseDto.getWorkoutId());
        Exercise exercise = Exercise.builder()
                .name(exerciseDto.getName())
                .sets(exerciseDto.getSets())
                .workout(workout)
                .user(user)
                .build();
        return exerciseRepository.save(exercise);
    }

    public List<Exercise> findAll() {
        User user = getCurrentUser();
        return exerciseRepository.findByUserId(user.getId());
    }

    public Exercise findById(UUID id) {
        User user = getCurrentUser();
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        checkOwnership(user, id);
        return exercise;
    }

    public Exercise update(UUID id, ExerciseDto exerciseDto) {
        try {
            User user = getCurrentUser();
            Exercise entity = exerciseRepository.getReferenceById(id);
            checkOwnership(user, id);
            updateData(entity, exerciseDto);
            return exerciseRepository.save(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

    public void delete(UUID id) {
        try {
            User user = getCurrentUser();
            exerciseRepository.getReferenceById(id);
            checkOwnership(user, id);
            exerciseRepository.deleteById(id);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

    private void updateData(Exercise entity, ExerciseDto obj) {
        entity.setName(obj.getName());
        entity.setSets(obj.getSets());
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
