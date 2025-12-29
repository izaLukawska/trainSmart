package org.lukawska.trainsmart.sharedpersistence.application.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long userId) {
        super(String.format("User not found for ID: %d", userId));
    }

    public UserNotFoundException() {
        super("User not found");
    }
}
