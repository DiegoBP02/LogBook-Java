package com.dev.logBook.dtos;

import com.dev.logBook.enums.Muscles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutDto {
    @NotBlank
    private Muscles muscle;
    @NotBlank
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
}
