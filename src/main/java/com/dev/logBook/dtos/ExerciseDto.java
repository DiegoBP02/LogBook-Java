package com.dev.logBook.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    @Digits(integer = 4, fraction = 3)
    private BigDecimal weight;
    @Min(0)
    @Max(10)
    private int rir;
}
