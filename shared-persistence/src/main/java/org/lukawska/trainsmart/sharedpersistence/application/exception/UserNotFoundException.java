package org.lukawska.trainsmart.sharedpersistence.application.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("User not found");
    }
}
