package org.lukawska.trainsmart.gymfinder.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.gymfinder.application.dto.GymLocationResponse;
import org.lukawska.trainsmart.gymfinder.model.FindGymResponse;

import java.util.Optional;

@UtilityClass
public class FindGymMapper {

    public static FindGymResponse mapToFindGymResponse(GymLocationResponse locationResponse, double distance) {
        String name = Optional.ofNullable(locationResponse.tags())
                              .map(tag -> tag.getOrDefault("name", "Unnamed Gym"))
                              .orElse("Unnamed Gym");

        return new FindGymResponse(name, locationResponse.lat(), locationResponse.lon(), distance);
    }
}
