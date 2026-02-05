package org.lukawska.trainsmart.gymfinder.presentation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.gymfinder.api.GymLocationApi;
import org.lukawska.trainsmart.gymfinder.application.service.GymLocationService;
import org.lukawska.trainsmart.gymfinder.model.FindGymRequest;
import org.lukawska.trainsmart.gymfinder.model.FindGymResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
public class GymLocationController implements GymLocationApi {

    private final GymLocationService gymLocationService;

    @Override
    public ResponseEntity<List<FindGymResponse>> getGymsNearby(FindGymRequest findGymRequest) {
        log.info("Looking for gyms around lat: {}, lon: {}, radius: {}m", findGymRequest.getUserLatitude(),
                 findGymRequest.getUserLongitude(), findGymRequest.getSearchRadiusMeters());

        return ResponseEntity.ok().body(gymLocationService.getGymsNearby(findGymRequest));
    }
}
