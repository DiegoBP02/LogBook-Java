package com.dev.logBook.dtos;

import com.dev.logBook.enums.Muscles;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutDto {
    @NotNull
    private Muscles muscle;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    @NotNull
    @Min(0)
    @Max(50)
    private int lowerRepsRange;
    @NotNull
    @Min(0)
    @Max(50)
    private int upperRepsRange;
}
