package com.dev.logBook.services;

import com.dev.logBook.ApplicationConfigTest;
import com.dev.logBook.dtos.ExerciseDto;
import com.dev.logBook.entities.Exercise;
import com.dev.logBook.entities.User;
import com.dev.logBook.entities.Workout;
import com.dev.logBook.entities.enums.Role;
import com.dev.logBook.enums.Muscles;
import com.dev.logBook.repositories.ExerciseRepository;
import com.dev.logBook.services.exceptions.ResourceNotFoundException;
import com.dev.logBook.services.exceptions.UnauthorizedAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ExerciseServiceTest extends ApplicationConfigTest {

    @Autowired
    private ExerciseService exerciseService;
    @MockBean
    private ExerciseRepository exerciseRepository;
    @MockBean
    private WorkoutService workoutService;

    User USER_RECORD = new User("username", "email", "password", Role.ROLE_USER);
    User USER_RECORD_2 = new User("username", "email", "password", Role.ROLE_USER);
    Workout WORKOUT_RECORD = Workout.builder()
            .date(LocalDate.now())
            .muscle(Muscles.chest)
            .user(USER_RECORD)
            .lowerRepsRange(8)
            .upperRepsRange(12)
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
    @DisplayName("should create an exercise")
    void create_successful() throws Exception {
        when(workoutService.findById(any(UUID.class))).thenReturn(WORKOUT_RECORD);
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(EXERCISE_RECORD);

        Exercise result = exerciseService.create(EXERCISE_DTO_RECORD);

        assertEquals(EXERCISE_RECORD, result);

        verify(workoutService, times(1)).findById(any(UUID.class));
        verify(exerciseRepository, times(1)).save(any(Exercise.class));
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException if workout does not exist")
    void create_invalidWorkout() throws Exception {
        when(workoutService.findById(any(UUID.class)))
                .thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> {
            exerciseService.create(EXERCISE_DTO_RECORD);
        });

        verify(workoutService, times(1)).findById(any(UUID.class));
        verify(exerciseRepository, never()).save(any(Exercise.class));
    }

    @Test
    @DisplayName("should return a list of exercises")
    void findAll_successful() throws Exception {
        List<Exercise> exerciseList = Collections.singletonList(EXERCISE_RECORD);
        when(exerciseRepository.findByUserId(any(UUID.class)))
                .thenReturn(exerciseList);

        List<Exercise> result = exerciseService.findAll();

        assertEquals(exerciseList, result);

        verify(exerciseRepository, times(1)).findByUserId(any(UUID.class));
    }

    @Test
    @DisplayName("should return an exercise")
    void findById_successful() throws Exception {
        when(exerciseRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(EXERCISE_RECORD));

        Exercise result = exerciseService.findById(UUID.randomUUID());

        assertEquals(EXERCISE_RECORD, result);

        verify(exerciseRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException if no exercise is found")
    void findById_noExerciseFound() throws Exception {
        assertThrows(ResourceNotFoundException.class, () -> {
            exerciseService.findById(UUID.randomUUID());
        });

        verify(exerciseRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("should throw UnauthorizedAccessException " +
            "if user is not the owner of the exercise")
    void findById_invalidCheckOwnership() throws Exception {
        when(authentication.getPrincipal()).thenReturn(USER_RECORD_2);
        when(exerciseRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(EXERCISE_RECORD));

        assertThrows(UnauthorizedAccessException.class, () -> {
            exerciseService.findById(UUID.randomUUID());
        });

        verify(exerciseRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("should update and return the updated exercise")
    void update_successful() throws Exception {
        when(exerciseRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(EXERCISE_RECORD));
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(EXERCISE_RECORD);

        EXERCISE_DTO_RECORD.setName("new name");

        Exercise result = exerciseService.update(UUID.randomUUID(), EXERCISE_DTO_RECORD);

        assertEquals(EXERCISE_DTO_RECORD.getName(), result.getName());

        verify(exerciseRepository, times(1)).findById(any(UUID.class));
        verify(exerciseRepository, times(1)).save(any(Exercise.class));
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException if no exercise is found")
    void update_noExerciseFound() throws Exception {
        assertThrows(ResourceNotFoundException.class, () -> {
            exerciseService.update(UUID.randomUUID(), EXERCISE_DTO_RECORD);
        });

        verify(exerciseRepository, times(1)).findById(any(UUID.class));
        verify(exerciseRepository, never()).save(any(Exercise.class));
    }

    @Test
    @DisplayName("should throw UnauthorizedAccessException " +
            "if user is not the owner of the exercise")
    void update_invalidCheckOwnership() throws Exception {
        when(exerciseRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(EXERCISE_RECORD));
        when(authentication.getPrincipal()).thenReturn(USER_RECORD_2);

        assertThrows(UnauthorizedAccessException.class, () -> {
            exerciseService.update(UUID.randomUUID(), EXERCISE_DTO_RECORD);
        });

        verify(exerciseRepository, times(1)).findById(any(UUID.class));
        verify(exerciseRepository, never()).save(any(Exercise.class));
    }

    @Test
    @DisplayName("should delete an exercise")
    void delete_successful() throws Exception {
        when(exerciseRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(EXERCISE_RECORD));

        exerciseService.delete(UUID.randomUUID());

        verify(exerciseRepository, times(1)).findById(any(UUID.class));
        verify(exerciseRepository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException if no exercise is found")
    void delete_noExerciseFound() throws Exception {
        assertThrows(ResourceNotFoundException.class, () -> {
            exerciseService.delete(UUID.randomUUID());
        });

        verify(exerciseRepository, times(1)).findById(any(UUID.class));
        verify(exerciseRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("should throw UnauthorizedAccessException " +
            "if user is not the owner of the exercise")
    void delete_invalidCheckOwnership() throws Exception {
        when(exerciseRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(EXERCISE_RECORD));
        when(authentication.getPrincipal()).thenReturn(USER_RECORD_2);

        assertThrows(UnauthorizedAccessException.class, () -> {
            exerciseService.delete(UUID.randomUUID());
        });

        verify(exerciseRepository, times(1)).findById(any(UUID.class));
        verify(exerciseRepository, never()).deleteById(any(UUID.class));

    }

}

