package org.lukawska.trainsmart.gymfinder.application.exception;

public class GymSearchException extends RuntimeException {

    public GymSearchException() {
        super("Connection issues with location provider");
    }
}
