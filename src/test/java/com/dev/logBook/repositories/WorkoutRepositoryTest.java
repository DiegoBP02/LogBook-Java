package com.dev.logBook.repositories;

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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;

@DataJpaTest
class WorkoutRepositoryTest {

    @Autowired
    private WorkoutRepository subject;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() throws Exception{
        subject.deleteAll();
    }

    @Test
    @DisplayName("should save a workout and get a list with the workout")
    void findByUserId() throws Exception {
        User USER_RECORD = new User("username", "email",
                "password", Role.ROLE_USER);
        userRepository.save(USER_RECORD);

        Workout WORKOUT_RECORD = Workout.builder()
                .date(LocalDate.now())
                .muscle(Muscles.chest)
                .user(USER_RECORD)
                .lowerRepsRange(8)
                .upperRepsRange(12)
                .build();

        subject.save(WORKOUT_RECORD);

        List<Workout> result = subject.findByUserId(USER_RECORD.getId());

        assertEquals(Collections.singletonList(WORKOUT_RECORD), result);
    }

    @Test
    @DisplayName("should find a workout by the date and user id")
    void findByDateAndUserId() throws Exception {
        User USER_RECORD = new User("username", "email",
                "password", Role.ROLE_USER);
        userRepository.save(USER_RECORD);

        Workout WORKOUT_RECORD = Workout.builder()
                .date(LocalDate.now())
                .muscle(Muscles.chest)
                .user(USER_RECORD)
                .lowerRepsRange(8)
                .upperRepsRange(12)
                .build();

        subject.save(WORKOUT_RECORD);

        Optional<Workout> result = subject.findByDateAndUserId
                (WORKOUT_RECORD.getDate(),USER_RECORD.getId());

        assertEquals(Optional.of(WORKOUT_RECORD), result);
    }

}