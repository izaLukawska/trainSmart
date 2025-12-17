package org.lukawska.trainsmart.trainingplan.presentation.controllers;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.trainingplan.application.dto.request.PagingRequest;
import org.lukawska.trainsmart.trainingplan.application.dto.request.UserExerciseFilterRequest;
import org.lukawska.trainsmart.trainingplan.application.dto.response.UserExerciseResponse;
import org.lukawska.trainsmart.trainingplan.application.service.UserExerciseService;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/exercises")
@Slf4j
public class UserExerciseController {

    private final UserExerciseService userExerciseService;

    @GetMapping
    public Slice<UserExerciseResponse> getAllUserExercisesByUserId(
            @PathVariable @Positive Long userId,
            @ModelAttribute PagingRequest pagingRequest,
            @ModelAttribute UserExerciseFilterRequest filterRequest) {
        log.info("Getting exercises for user {} for keyword {}, muscle group {} type {} sorted by {} with direction {}",
                 userId, filterRequest.keyword(), filterRequest.muscleGroup(), filterRequest.exerciseType(),
                 pagingRequest.sortBy(), pagingRequest.direction());
        return userExerciseService.getAllUserExercisesByUserId(userId, pagingRequest, filterRequest);
    }

    @GetMapping("/{exerciseName}")
    public UserExerciseResponse getUserExerciseByUserIdAndExerciseName(@PathVariable @Positive Long userId,
                                                                       @PathVariable String exerciseName) {
        log.info("Getting user exercise with name {} for user {}", exerciseName, userId);
        return userExerciseService.getUserExerciseByUserIdAndExerciseName(userId, exerciseName);
    }
}
