package com.dev.logBook.services;


import com.dev.logBook.ApplicationConfigTest;
import com.dev.logBook.dtos.ExerciseDto;
import com.dev.logBook.dtos.WorkoutDto;
import com.dev.logBook.entities.Exercise;
import com.dev.logBook.entities.User;
import com.dev.logBook.entities.Workout;
import com.dev.logBook.entities.enums.Role;
import com.dev.logBook.enums.Muscles;
import com.dev.logBook.repositories.WorkoutRepository;
import com.dev.logBook.services.exceptions.ResourceNotFoundException;
import com.dev.logBook.services.exceptions.UnauthorizedAccessException;
import com.dev.logBook.services.exceptions.UniqueConstraintViolationError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WorkoutServiceTest extends ApplicationConfigTest {

    User USER_RECORD = new User("username", "email", "password", Role.ROLE_USER);
    User USER_RECORD_2 = new User("username", "email", "password", Role.ROLE_USER);
    WorkoutDto WORKOUT_DTO_RECORD = WorkoutDto.builder()
            .date(LocalDate.now())
            .muscle(Muscles.CHEST)
            .lowerRepsRange(8)
            .upperRepsRange(12)
            .build();
    Workout WORKOUT_RECORD = Workout.builder()
            .date(WORKOUT_DTO_RECORD.getDate())
            .muscle(WORKOUT_DTO_RECORD.getMuscle())
            .user(USER_RECORD)
            .lowerRepsRange(WORKOUT_DTO_RECORD.getLowerRepsRange())
            .upperRepsRange(WORKOUT_DTO_RECORD.getUpperRepsRange())
            .build();
    ExerciseDto EXERCISE_DTO_RECORD = ExerciseDto.builder()
            .name("name")
            .weight(50)
            .reps(10)
            .rir(0)
            .workoutId(WORKOUT_RECORD.getId())
            .build();
    Exercise EXERCISE_RECORD = Exercise.builder()
            .name(EXERCISE_DTO_RECORD.getName())
            .weight(EXERCISE_DTO_RECORD.getWeight())
            .reps(EXERCISE_DTO_RECORD.getReps())
            .rir(EXERCISE_DTO_RECORD.getRir())
            .workout(WORKOUT_RECORD)
            .user(USER_RECORD)
            .build();
    Authentication authentication;
    SecurityContext securityContext;
    @Autowired
    private WorkoutService workoutService;
    @MockBean
    private WorkoutRepository workoutRepository;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(USER_RECORD, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(USER_RECORD_2, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(WORKOUT_RECORD, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(EXERCISE_RECORD, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(EXERCISE_DTO_RECORD, "workoutId", UUID.randomUUID());

        // implements getCurrentUser behaviour
        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(USER_RECORD);
        securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("should create a workout")
    void create_successful() throws Exception {
        when(workoutRepository.save(any(Workout.class))).thenReturn(WORKOUT_RECORD);

        Workout result = workoutService.create(WORKOUT_DTO_RECORD);

        assertEquals(WORKOUT_RECORD, result);

        verify(workoutRepository, times(1)).save(any(Workout.class));
    }

    @Test
    @DisplayName("should throw UniqueConstraintViolationError " +
            "if workout with the same date already exists")
    void crete_workoutAlreadyExists() {
        when(workoutRepository.save(any(Workout.class)))
                .thenThrow(DataIntegrityViolationException.class);

        UniqueConstraintViolationError exception =
                assertThrows(UniqueConstraintViolationError.class, () -> {
                    workoutService.create(WORKOUT_DTO_RECORD);
                });

        verify(workoutRepository, times(1)).save(any(Workout.class));
    }


    @Test
    @DisplayName("should return a list of workouts")
    void findAll_successful() throws Exception {
        List<Workout> workoutList = Collections.singletonList(WORKOUT_RECORD);
        when(workoutRepository.findByUserId(any(UUID.class)))
                .thenReturn(workoutList);

        List<Workout> result = workoutService.findAll();

        assertEquals(workoutList, result);

        verify(workoutRepository, times(1)).findByUserId(any(UUID.class));
    }

    @Test
    @DisplayName("should return a workout")
    void findById_successful() throws Exception {
        when(workoutRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(WORKOUT_RECORD));

        Workout result = workoutService.findById(UUID.randomUUID());

        assertEquals(WORKOUT_RECORD, result);

        verify(workoutRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException if no workout is found")
    void findById_noWorkoutFound() throws Exception {
        assertThrows(ResourceNotFoundException.class, () -> {
            workoutService.findById(UUID.randomUUID());
        });

        verify(workoutRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("should throw UnauthorizedAccessException " +
            "if user is not the owner of the workout")
    void findById_invalidCheckOwnership() throws Exception {
        when(authentication.getPrincipal()).thenReturn(USER_RECORD_2);
        when(workoutRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(WORKOUT_RECORD));

        assertThrows(UnauthorizedAccessException.class, () -> {
            workoutService.findById(UUID.randomUUID());
        });

        verify(workoutRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("should return a workout")
    void findByDateAndMuscleAndUserId_successful() throws Exception {
        when(workoutRepository.findByDateAndMuscleAndUserId
                (any(LocalDate.class), any(Muscles.class),any(UUID.class)))
                .thenReturn(Optional.of(WORKOUT_RECORD));

        Workout result = workoutService.findByDateAndMuscle
                (WORKOUT_RECORD.getDate(), WORKOUT_RECORD.getMuscle());

        assertEquals(WORKOUT_RECORD, result);

        verify(workoutRepository, times(1))
                .findByDateAndMuscleAndUserId(any(LocalDate.class), any(Muscles.class),any(UUID.class));
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException if no workout is found")
    void findByDateAndUserId_noWorkoutFound() throws Exception {
        assertThrows(ResourceNotFoundException.class, () -> {
            workoutService.findByDateAndMuscle(WORKOUT_RECORD.getDate(), WORKOUT_RECORD.getMuscle());
        });

        verify(workoutRepository, times(1))
                .findByDateAndMuscleAndUserId(any(LocalDate.class), any(Muscles.class),any(UUID.class));
    }

    @Test
    @DisplayName("should throw UnauthorizedAccessException " +
            "if user is not the owner of the workout")
    void findByDateAndUserId_invalidCheckOwnership() throws Exception {
        when(authentication.getPrincipal()).thenReturn(USER_RECORD_2);
        when(workoutRepository.findByDateAndMuscleAndUserId
                (any(LocalDate.class), any(Muscles.class),any(UUID.class)))
                .thenReturn(Optional.of(WORKOUT_RECORD));

        assertThrows(UnauthorizedAccessException.class, () -> {
            workoutService.findByDateAndMuscle(WORKOUT_RECORD.getDate(), WORKOUT_RECORD.getMuscle());
        });

        verify(workoutRepository, times(1))
                .findByDateAndMuscleAndUserId(any(LocalDate.class), any(Muscles.class),any(UUID.class));
    }

    @Test
    @DisplayName("should update and return the updated workout")
    void update_successful() throws Exception {
        when(workoutRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(WORKOUT_RECORD));
        when(workoutRepository.save(any(Workout.class))).thenReturn(WORKOUT_RECORD);

        WORKOUT_DTO_RECORD.setMuscle(Muscles.BACK);

        Workout result = workoutService.update(UUID.randomUUID(), WORKOUT_DTO_RECORD);

        assertEquals(WORKOUT_DTO_RECORD.getMuscle(), result.getMuscle());

        verify(workoutRepository, times(1)).findById(any(UUID.class));
        verify(workoutRepository, times(1)).save(any(Workout.class));
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException if no workout is found")
    void update_noWorkoutFound() throws Exception {
        assertThrows(ResourceNotFoundException.class, () -> {
            workoutService.update(UUID.randomUUID(), WORKOUT_DTO_RECORD);
        });

        verify(workoutRepository, times(1)).findById(any(UUID.class));
        verify(workoutRepository, never()).save(any(Workout.class));
    }

    @Test
    @DisplayName("should throw UnauthorizedAccessException " +
            "if user is not the owner of the workout")
    void update_invalidCheckOwnership() throws Exception {
        when(workoutRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(WORKOUT_RECORD));
        when(authentication.getPrincipal()).thenReturn(USER_RECORD_2);

        assertThrows(UnauthorizedAccessException.class, () -> {
            workoutService.update(UUID.randomUUID(), WORKOUT_DTO_RECORD);
        });

        verify(workoutRepository, times(1)).findById(any(UUID.class));
        verify(workoutRepository, never()).save(any(Workout.class));
    }

    @Test
    @DisplayName("should delete a workout")
    void delete_successful() throws Exception {
        when(workoutRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(WORKOUT_RECORD));

        workoutService.delete(UUID.randomUUID());

        verify(workoutRepository, times(1)).findById(any(UUID.class));
        verify(workoutRepository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException if no workout is found")
    void delete_noWorkoutFound() throws Exception {
        assertThrows(ResourceNotFoundException.class, () -> {
            workoutService.delete(UUID.randomUUID());
        });

        verify(workoutRepository, times(1)).findById(any(UUID.class));
        verify(workoutRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("should throw UnauthorizedAccessException " +
            "if user is not the owner of the workout")
    void delete_invalidCheckOwnership() throws Exception {
        when(workoutRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(WORKOUT_RECORD));
        when(authentication.getPrincipal()).thenReturn(USER_RECORD_2);

        assertThrows(UnauthorizedAccessException.class, () -> {
            workoutService.delete(UUID.randomUUID());
        });

        verify(workoutRepository, times(1)).findById(any(UUID.class));
        verify(workoutRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("should return a list of exercises")
    void getExercisesOutsideRepsRange_successful() throws Exception {
        Exercise EXERCISE_RECORD_OUTSIDE_REP_RANGE = Exercise.builder()
                .name(EXERCISE_DTO_RECORD.getName())
                .weight(EXERCISE_DTO_RECORD.getWeight())
                .reps(EXERCISE_DTO_RECORD.getReps() + 10)
                .rir(EXERCISE_DTO_RECORD.getRir())
                .workout(WORKOUT_RECORD)
                .user(USER_RECORD)
                .build();

        List<Exercise> exercises = Arrays.asList(EXERCISE_RECORD, EXERCISE_RECORD_OUTSIDE_REP_RANGE);

        WORKOUT_RECORD.setExercises(exercises);

        when(workoutRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(WORKOUT_RECORD));

        List<Exercise> result = workoutService.getExercisesOutsideRepsRange(UUID.randomUUID());

        assertEquals(Collections.singletonList(EXERCISE_RECORD_OUTSIDE_REP_RANGE), result);

        verify(workoutRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException if no workout is found")
    void getExercisesOutsideRepsRange_noWorkoutFound() throws Exception {
        assertThrows(ResourceNotFoundException.class, () -> {
            workoutService.getExercisesOutsideRepsRange(UUID.randomUUID());
        });

        verify(workoutRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("should throw UnauthorizedAccessException " +
            "if user is not the owner of the workout")
    void getExercisesOutsideRepsRange_invalidCheckOwnership() throws Exception {
        when(authentication.getPrincipal()).thenReturn(USER_RECORD_2);
        when(workoutRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(WORKOUT_RECORD));

        assertThrows(UnauthorizedAccessException.class, () -> {
            workoutService.getExercisesOutsideRepsRange(UUID.randomUUID());
        });

        verify(workoutRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("should return a hashmap containing the volume load")
    void calculateVolumeLoad_successful() throws Exception {
        Exercise EXERCISE_RECORD_2 = Exercise.builder()
                .name(EXERCISE_DTO_RECORD.getName())
                .weight(EXERCISE_DTO_RECORD.getWeight() + 5)
                .reps(EXERCISE_DTO_RECORD.getReps() + 5)
                .rir(EXERCISE_DTO_RECORD.getRir())
                .workout(WORKOUT_RECORD)
                .user(USER_RECORD)
                .build();

        Exercise EXERCISE_RECORD_3 = Exercise.builder()
                .name("random name")
                .weight(EXERCISE_DTO_RECORD.getWeight() + 5)
                .reps(EXERCISE_DTO_RECORD.getReps() + 5)
                .rir(EXERCISE_DTO_RECORD.getRir())
                .workout(WORKOUT_RECORD)
                .user(USER_RECORD)
                .build();

        List<Exercise> exercises = Arrays.asList
                (EXERCISE_RECORD, EXERCISE_RECORD_2, EXERCISE_RECORD_3);

        WORKOUT_RECORD.setExercises(exercises);

        HashMap<String, Integer> expectedHashMap = new HashMap<>();
        expectedHashMap.put(EXERCISE_RECORD.getName(),
                EXERCISE_RECORD.getReps() * EXERCISE_RECORD.getWeight()
                        + EXERCISE_RECORD_2.getReps() * EXERCISE_RECORD_2.getWeight());
        expectedHashMap.put(EXERCISE_RECORD_3.getName(),
                EXERCISE_RECORD_3.getReps() * EXERCISE_RECORD_3.getWeight());

        when(workoutRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(WORKOUT_RECORD));

        HashMap<String, Integer> result = workoutService.calculateVolumeLoad(UUID.randomUUID());

        assertEquals(expectedHashMap, result);

        verify(workoutRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException if no workout is found")
    void calculateVolumeLoad_noWorkoutFound() throws Exception {
        assertThrows(ResourceNotFoundException.class, () -> {
            workoutService.calculateVolumeLoad(UUID.randomUUID());
        });

        verify(workoutRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("should throw UnauthorizedAccessException " +
            "if user is not the owner of the workout")
    void calculateVolumeLoad_invalidCheckOwnership() throws Exception {
        when(authentication.getPrincipal()).thenReturn(USER_RECORD_2);
        when(workoutRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(WORKOUT_RECORD));

        assertThrows(UnauthorizedAccessException.class, () -> {
            workoutService.calculateVolumeLoad(UUID.randomUUID());
        });

        verify(workoutRepository, times(1)).findById(any(UUID.class));
    }

}

