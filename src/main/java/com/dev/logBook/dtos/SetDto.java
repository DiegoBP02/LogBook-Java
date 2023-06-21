package com.dev.logBook.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetDto {
    @NotBlank
    @Min(0)
    @Max(50)
    private int reps;
    @NotBlank
    @Min(0)
    @Max(1500)
    private int weight;
    @NotBlank
    @Min(0)
    @Max(10)
    private int rir;
}
