package com.dev.logBook.dtos;

import com.dev.logBook.entities.models.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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
    private List<@Valid Set> sets;
    @NotBlank
    @com.dev.logBook.dtos.utils.UUID
    private UUID workoutId;
}
