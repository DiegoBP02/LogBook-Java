package com.dev.logBook.services.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseComparator {
    private String name;
    private int repsDifference;
    private BigDecimal weightDifference;
    private int rirDifference;
}
