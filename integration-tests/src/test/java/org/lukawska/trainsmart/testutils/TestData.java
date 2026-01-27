package org.lukawska.trainsmart.testutils;

import lombok.experimental.UtilityClass;

import java.util.Random;
import java.util.UUID;

@UtilityClass
public class TestData {

    public static String rawPassword() {
        return "password" + randomInteger();
    }

    public static String email() {
        return randomInteger() + "@email.com";
    }

    public static String username() {
        return "user" + randomInteger();
    }

    public static String tokenValue() {
        return randomInteger() + "tokenValue";
    }

    public static String exerciseName() {
        return "exercise" + randomInteger();
    }

    public static String text() {
        return UUID.randomUUID().toString();
    }

    private Integer randomInteger() {
        return new Random().nextInt();
    }
}
