package com.dev.logBook.services;

import com.dev.logBook.dtos.WorkoutDto;
import com.dev.logBook.entities.Exercise;
import com.dev.logBook.entities.User;
import com.dev.logBook.entities.Workout;
import com.dev.logBook.repositories.WorkoutRepository;
import com.dev.logBook.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.dev.logBook.services.utils.CheckOwnership.checkOwnership;

@Service
public class WorkoutService {

    @Autowired
    private WorkoutRepository workoutRepository;

    public Workout create(WorkoutDto workoutDTO) {
        User user = getCurrentUser();
        Workout workout = Workout.builder()
                .date(workoutDTO.getDate())
                .muscle(workoutDTO.getMuscle())
                .user(user)
                .lowerRepsRange(workoutDTO.getLowerRepsRange())
                .upperRepsRange(workoutDTO.getUpperRepsRange())
                .build();
        return workoutRepository.save(workout);
    }

    public List<Workout> findAll() {
        User user = getCurrentUser();
        return workoutRepository.findByUserId(user.getId());
    }

    public Workout findById(UUID id) {
        User user = getCurrentUser();
        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        checkOwnership(user, workout.getUser().getId());
        return workout;
    }

    public Workout findByDateAndUserId(LocalDate date) {
        User user = getCurrentUser();
        return workoutRepository.findByDateAndUserId(date, user.getId())
                .orElseThrow(ResourceNotFoundException::new);
    }

    public Workout update(UUID id, WorkoutDto workoutDto) {
        try {
            User user = getCurrentUser();
            Workout entity = workoutRepository.getReferenceById(id);
            checkOwnership(user, entity.getUser().getId());
            updateData(entity, workoutDto);
            return workoutRepository.save(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

    public void delete(UUID id) {
        try {
            User user = getCurrentUser();
            Workout workout = workoutRepository.getReferenceById(id);
            checkOwnership(user, workout.getUser().getId());
            workoutRepository.deleteById(id);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

    public List<Exercise> getExercisesOutsideRepsRange(UUID workoutId) {
        Workout workout = findById(workoutId);
        List<Exercise> exercises = workout.getExercises();
        List<Exercise> exercisesOutsideRepsRange = new ArrayList<>();
        exercises.forEach(exercise -> {
            if (exercise.getReps() < workout.getLowerRepsRange() ||
                    exercise.getReps() > workout.getUpperRepsRange()) {
                exercisesOutsideRepsRange.add(exercise);
            }
        });

        return exercisesOutsideRepsRange;
    }

    public HashMap<String, Integer> calculateVolumeLoad(UUID workoutId) {
        Workout workout = findById(workoutId);
        List<Exercise> exercises = workout.getExercises();
        HashMap<String, Integer> result = new HashMap<>();
        exercises.forEach(exercise -> {
            String name = exercise.getName();
            int weight = exercise.getWeight();
            int reps = exercise.getReps();
            int volume = weight * reps;

            if (result.containsKey(name)) {
                int previousVolume = result.get(name);
                volume += previousVolume;
            }

            result.put(name, volume);
        });
        return result;
    }

    private void updateData(Workout entity, WorkoutDto obj) {
        entity.setDate(obj.getDate());
        entity.setMuscle(obj.getMuscle());
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
    }
}
