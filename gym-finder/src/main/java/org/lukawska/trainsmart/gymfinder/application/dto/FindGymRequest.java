package org.lukawska.trainsmart.gymfinder.application.dto;

public record FindGymRequest(int searchRadiusMeters, double userLatitude, double userLongitude) {
}
