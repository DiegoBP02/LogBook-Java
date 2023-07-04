package com.dev.logBook.controller.exceptions;

import com.dev.logBook.enums.Muscles;

public class InvalidMuscleEnumException extends RuntimeException{
    public InvalidMuscleEnumException(String invalidMuscle){
        super("Invalid muscle: " + invalidMuscle + ". Muscles available: " + getAllMuscles());
    }

    private static String getAllMuscles() {
        StringBuilder muscles = new StringBuilder();
        Muscles[] allMuscles = Muscles.values();
        for (int i = 0; i < allMuscles.length; i++) {
            muscles.append(allMuscles[i].name());
            if (i < allMuscles.length - 1) {
                muscles.append(", ");
            }
        }
        return muscles.toString();
    }
}
