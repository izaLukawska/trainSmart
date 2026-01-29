package org.lukawska.trainsmart.gymfinder.application.service;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.gymfinder.application.dto.GymContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/gym")
@RequiredArgsConstructor
@RestController
public class GymLocationController {

    private final GymLocationService gymLocationService;

    @GetMapping
    public List<GymContext> getNearbyGyms(@RequestParam double lat, @RequestParam double lon) {
        return gymLocationService.getTop5Gyms(lat, lon);
    }
}
