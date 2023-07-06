package com.dev.logBook.services;

import com.dev.logBook.dtos.WorkoutDto;
import com.dev.logBook.entities.Exercise;
import com.dev.logBook.entities.User;
import com.dev.logBook.entities.Workout;
import com.dev.logBook.enums.Muscles;
import com.dev.logBook.repositories.WorkoutRepository;
import com.dev.logBook.services.exceptions.ResourceNotFoundException;
import com.dev.logBook.services.exceptions.UniqueConstraintViolationError;
import com.dev.logBook.services.utils.ExerciseComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
        try {
            User user = getCurrentUser();
            Workout workout = Workout.builder()
                    .date(workoutDTO.getDate())
                    .muscle(workoutDTO.getMuscle())
                    .user(user)
                    .lowerRepsRange(workoutDTO.getLowerRepsRange())
                    .upperRepsRange(workoutDTO.getUpperRepsRange())
                    .build();
            return workoutRepository.save(workout);
        } catch (DataIntegrityViolationException e) {
            throw new UniqueConstraintViolationError("workout", "date");
        }
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

    public Workout findByDateAndMuscle(LocalDate date, Muscles muscle) {
        User user = getCurrentUser();
        Workout workout = workoutRepository.findByDateAndMuscleAndUserId(date, muscle, user.getId())
                .orElseThrow(ResourceNotFoundException::new);
        checkOwnership(user, workout.getUser().getId());
        return workout;
    }

    public Workout update(UUID id, WorkoutDto workoutDto) {
        Workout workout = findById(id);
        updateData(workout, workoutDto);
        return workoutRepository.save(workout);
    }

    private void updateData(Workout entity, WorkoutDto obj) {
        entity.setDate(obj.getDate());
        entity.setMuscle(obj.getMuscle());
    }

    public void delete(UUID id) {
        findById(id);
        workoutRepository.deleteById(id);
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

    public List<ExerciseComparator> compareWorkouts(UUID oldWorkoutId, UUID currentWorkoutId) {
        List<Exercise> oldWorkoutExercises = getExercisesFromWorkout(oldWorkoutId);
        List<Exercise> currentWorkoutExercises = getExercisesFromWorkout(currentWorkoutId);

        List<ExerciseComparator> result = new ArrayList<>();

        for (Exercise oldExercise : oldWorkoutExercises) {
            for (Exercise currentExercise : currentWorkoutExercises) {
                if (oldExercise.getName().equalsIgnoreCase(currentExercise.getName())) {
                    ExerciseComparator exerciseComparator = new ExerciseComparator();
                    exerciseComparator.setName(currentExercise.getName());

                    if (currentExercise.getReps() != oldExercise.getReps()) {
                        int repsDifference = currentExercise.getReps() - oldExercise.getReps();
                        exerciseComparator.setRepsDifference(repsDifference);
                    }

                    if (currentExercise.getWeight() != oldExercise.getWeight()) {
                        int weightDifference = currentExercise.getWeight() - oldExercise.getWeight();
                        exerciseComparator.setWeightDifference(weightDifference);
                    }

                    if (currentExercise.getRir() != oldExercise.getRir()) {
                        int rirDifference = currentExercise.getRir() - oldExercise.getRir();
                        exerciseComparator.setRirDifference(rirDifference);
                    }

                    result.add(exerciseComparator);
                    break;
                }
            }
        }

        return result;
    }

    public List<Exercise> getUniqueWorkoutExercises
            (UUID oldWorkoutId, UUID currentWorkoutId, boolean isOldWorkout) {
        List<Exercise> workoutExercises = getExercisesFromWorkout(isOldWorkout ? oldWorkoutId : currentWorkoutId);
        List<Exercise> otherWorkoutExercises = getExercisesFromWorkout(isOldWorkout ? currentWorkoutId : oldWorkoutId);

        List<Exercise> commonExercises =
                getCommonExercises(workoutExercises, otherWorkoutExercises);

        return removeExercisesInCommon(workoutExercises, commonExercises);
    }

    private List<Exercise> getExercisesFromWorkout(UUID workoutId) {
        return findById(workoutId).getExercises();
    }

    private List<Exercise> getCommonExercises
            (List<Exercise> workoutExercises, List<Exercise> otherWorkoutExercises) {
        List<Exercise> commonExercises = new ArrayList<>();

        for (Exercise oldExercise : workoutExercises) {
            for (Exercise currentExercise : otherWorkoutExercises) {
                if (oldExercise.getName().equalsIgnoreCase(currentExercise.getName())) {
                    commonExercises.add(oldExercise);
                    break;
                }
            }
        }

        return commonExercises;
    }

    private List<Exercise> removeExercisesInCommon
            (List<Exercise> exercisesList, List<Exercise> commonExercises) {
        List<Exercise> exercises = new ArrayList<>(exercisesList);

        exercises.removeIf(currentExercise -> {
            for (Exercise commonExercise : commonExercises) {
                if (currentExercise.getName().equalsIgnoreCase(commonExercise.getName())) {
                    return true;
                }
            }
            return false;
        });

        return exercises;
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
    }
}
