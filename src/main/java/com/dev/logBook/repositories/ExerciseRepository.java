package com.dev.logBook.repositories;

import com.dev.logBook.entities.Exercise;
import com.dev.logBook.entities.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {
}
