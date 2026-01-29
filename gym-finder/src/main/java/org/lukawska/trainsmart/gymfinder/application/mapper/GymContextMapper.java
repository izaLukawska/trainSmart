package org.lukawska.trainsmart.gymfinder.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.gymfinder.application.dto.GymContextResponse;
import org.lukawska.trainsmart.gymfinder.application.dto.LocationResponse;

import java.util.Optional;

@UtilityClass
public class GymContextMapper {

    public static GymContextResponse mapToGymContextResponse(LocationResponse locationResponse, double distance) {
        String name = Optional.ofNullable(locationResponse.tags())
                              .map(tag -> tag.getOrDefault("name", "Unnamed Gym"))
                              .orElse("Unnamed Gym");

        return new GymContextResponse(name, locationResponse.lat(), locationResponse.lon(), distance);
    }
}
