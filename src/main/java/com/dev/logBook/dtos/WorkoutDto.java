package com.dev.logBook.dtos;

import com.dev.logBook.entities.Exercise;
import com.dev.logBook.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutDto {
    @NotBlank
    @com.dev.logBook.helpers.UUID
    private UUID muscleId;
    @NotBlank
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    @com.dev.logBook.helpers.UUID
    private User userId;
}
