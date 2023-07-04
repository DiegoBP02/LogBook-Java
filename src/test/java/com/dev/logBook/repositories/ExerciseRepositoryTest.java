package com.dev.logBook.repositories;

import com.dev.logBook.entities.Exercise;
import com.dev.logBook.entities.User;
import com.dev.logBook.entities.Workout;
import com.dev.logBook.entities.enums.Role;
import com.dev.logBook.enums.Muscles;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ExerciseRepositoryTest {

    @Autowired
    private ExerciseRepository subject;

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() throws Exception{
        subject.deleteAll();
    }

    @Test
    @DisplayName("should return a list of exercises by user id")
    void findByUserId() throws Exception{
        User USER_RECORD = new User("username", "email",
                "password", Role.ROLE_USER);
        userRepository.save(USER_RECORD);

        Workout WORKOUT_RECORD = Workout.builder()
                .date(LocalDate.now())
                .muscle(Muscles.CHEST)
                .user(USER_RECORD)
                .lowerRepsRange(8)
                .upperRepsRange(12)
                .build();
        workoutRepository.save(WORKOUT_RECORD);

        Exercise EXERCISE_RECORD = Exercise.builder()
                .name("name")
                .weight(50)
                .reps(10)
                .rir(0)
                .workout(WORKOUT_RECORD)
                .user(USER_RECORD)
                .build();

        subject.save(EXERCISE_RECORD);

        List<Exercise> result = subject.findByUserId(USER_RECORD.getId());

        assertEquals(Collections.singletonList(EXERCISE_RECORD), result);
    }
}