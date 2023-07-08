package com.dev.logBook.repositories;

import com.dev.logBook.entities.Exercise;
import com.dev.logBook.entities.User;
import com.dev.logBook.entities.Workout;
import com.dev.logBook.entities.enums.Role;
import com.dev.logBook.enums.Muscles;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class WorkoutRepositoryTest {

    @Autowired
    private WorkoutRepository subject;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    User USER_RECORD;
    Workout WORKOUT_RECORD;
    Exercise EXERCISE_RECORD;

    @BeforeEach
    void setup() throws Exception {
        USER_RECORD = new User("username", "email",
                "password", Role.ROLE_USER);
        userRepository.save(USER_RECORD);

        WORKOUT_RECORD = Workout.builder()
                .date(LocalDate.now())
                .muscle(Muscles.CHEST)
                .user(USER_RECORD)
                .lowerRepsRange(8)
                .upperRepsRange(12)
                .build();

        EXERCISE_RECORD = Exercise.builder()
                .name("name")
                .weight(50)
                .reps(10)
                .rir(0)
                .workout(WORKOUT_RECORD)
                .user(USER_RECORD)
                .build();

        exerciseRepository.save(EXERCISE_RECORD);

        WORKOUT_RECORD.setExercises(Collections.singletonList(EXERCISE_RECORD));
        subject.save(WORKOUT_RECORD);
    }

    @AfterEach
    void tearDown() throws Exception {
        subject.deleteAll();
    }

    @Test
    @DisplayName("should save a workout and get a list with the workout")
    void findByUserId() throws Exception {
        List<Workout> result = subject.findByUserId(USER_RECORD.getId());

        assertEquals(Collections.singletonList(WORKOUT_RECORD), result);
    }

    @Test
    @DisplayName("should find a workout by the date, muscle and user id")
    void findByDateAndMuscleAndUserId() throws Exception {
        Optional<Workout> result = subject.findByDateAndMuscleAndUserId
                (WORKOUT_RECORD.getDate(), WORKOUT_RECORD.getMuscle(), USER_RECORD.getId());

        assertEquals(Optional.of(WORKOUT_RECORD), result);
    }

    @Test
    @DisplayName("should return a list of workouts by the muscle and user id")
    void findByMuscleAndUserId() throws Exception {
        List<Workout> result = subject.findByMuscleAndUserId
                (WORKOUT_RECORD.getMuscle(), USER_RECORD.getId());

        assertEquals(Collections.singletonList(WORKOUT_RECORD), result);
    }

}