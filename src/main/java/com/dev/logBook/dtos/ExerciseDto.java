package com.dev.logBook.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    @NotNull
    @Min(0)
    @Max(10)
    private int rir;
}
