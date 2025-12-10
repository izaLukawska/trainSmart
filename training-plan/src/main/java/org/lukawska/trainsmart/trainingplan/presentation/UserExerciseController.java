package org.lukawska.trainsmart.trainingplan.presentation;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.trainingplan.application.dto.UserExerciseResponse;
import org.lukawska.trainsmart.trainingplan.application.service.UserExerciseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/exercises")
public class UserExerciseController {

    private final UserExerciseService userExerciseService;

    @PostMapping
    public ResponseEntity<Integer> syncExercises(@PathVariable Long userId) {
        int exercises = userExerciseService.syncUserExercise(userId).size();
        return ResponseEntity.status(201).body(exercises);
    }

    @GetMapping
    public List<UserExerciseResponse> getUserExercises(@PathVariable Long userId,
                                                       @RequestParam(required = false) Boolean enabled) {
        return userExerciseService.getUserExercisesResponse(userId, enabled);
    }
}
