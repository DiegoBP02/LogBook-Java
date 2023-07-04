package com.dev.logBook.repositories;

import com.dev.logBook.entities.Workout;
import com.dev.logBook.enums.Muscles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, UUID> {
    List<Workout> findByUserId(UUID userId);

    Optional<Workout> findByDateAndMuscleAndUserId(LocalDate date, Muscles muscle, UUID userId);

}
