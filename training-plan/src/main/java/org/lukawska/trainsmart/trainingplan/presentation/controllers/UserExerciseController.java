package org.lukawska.trainsmart.trainingplan.presentation.controllers;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.trainingplan.application.dto.response.UserExerciseResponse;
import org.lukawska.trainsmart.trainingplan.application.service.UserExerciseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/exercises")
public class UserExerciseController {

    private final UserExerciseService userExerciseService;

    @GetMapping
    public List<UserExerciseResponse> getUserExercises(@PathVariable Long userId,
                                                       @RequestParam(required = false) Boolean enabled) {
        return userExerciseService.getUserExercisesResponse(userId, enabled);
    }
}
