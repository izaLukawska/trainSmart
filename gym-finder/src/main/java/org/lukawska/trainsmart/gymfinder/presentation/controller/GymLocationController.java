package org.lukawska.trainsmart.gymfinder.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.gymfinder.application.dto.FindGymRequest;
import org.lukawska.trainsmart.gymfinder.application.dto.GymContextResponse;
import org.lukawska.trainsmart.gymfinder.application.service.GymLocationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/gym")
@RequiredArgsConstructor
@RestController
public class GymLocationController {

    private final GymLocationService gymLocationService;

    @GetMapping
    public List<GymContextResponse> getNearbyGyms(@RequestBody @Valid FindGymRequest gymContextRequest) {
        return gymLocationService.getTop5Gyms(gymContextRequest);
    }
}
