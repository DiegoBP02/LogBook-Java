package com.dev.logBook.dtos;

import com.dev.logBook.entities.Workout;
import com.dev.logBook.entities.models.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.ElementCollection;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDto {
    @NotBlank
    private String name;
    @NotBlank
    @com.dev.logBook.helpers.UUID
    private UUID muscleId;
    @NotBlank
    private List<@Valid SetDto> sets;
    @NotBlank
    @com.dev.logBook.helpers.UUID
    private UUID workoutId;
}
