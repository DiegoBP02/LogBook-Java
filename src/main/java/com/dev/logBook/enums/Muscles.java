package com.dev.logBook.enums;

public enum Muscles {
    CHEST(1),
    BACK(2),
    SHOULDERS(3),
    LEGS(4),
    BICEPS(5),
    TRICEPS(6),
    CALVES(7),
    ABDOMINAL(8),
    FULL_BODY(9);

    private int code;

    Muscles(int code) {
        this.code = code;
    }

    public static Muscles valueOf(int code) {
        for (Muscles value : Muscles.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid muscle enum code");
    }

    public int getCode() {
        return code;
    }
}
