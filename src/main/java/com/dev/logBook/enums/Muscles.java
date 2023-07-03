package com.dev.logBook.enums;

public enum Muscles {
    chest(1),
    back(2),
    shoulders(3),
    legs(4),
    biceps(5),
    triceps(6),
    calves(7),
    abdominal(8),
    fullBody(9);

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
