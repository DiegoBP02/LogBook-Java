package com.dev.logBook.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDto {
    @NotBlank
    private String name;
    @NotNull
    private UUID workoutId;
    @NotNull
    @Min(0)
    @Max(50)
    private int reps;
    @NotNull
    @Min(0)
    @Max(1500)
    private int weight;
    @Min(0)
    @Max(10)
    private int rir;
}
