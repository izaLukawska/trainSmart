package org.lukawska.trainsmart.trainingplan.presentation.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.trainingplan.api.UserExerciseApi;
import org.lukawska.trainsmart.trainingplan.application.service.UserExerciseService;
import org.lukawska.trainsmart.trainingplan.model.PagingRequest;
import org.lukawska.trainsmart.trainingplan.model.SliceUserExerciseResponse;
import org.lukawska.trainsmart.trainingplan.model.UserExerciseFilterRequest;
import org.lukawska.trainsmart.trainingplan.model.UserExerciseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserExerciseController implements UserExerciseApi {

    private final UserExerciseService userExerciseService;

    @Override
    public ResponseEntity<SliceUserExerciseResponse> getAllUserExercisesByUserId(
            Long userId, PagingRequest pagingRequest, UserExerciseFilterRequest userExerciseFilterRequest) {
        log.info("Received get all user exercise request");
        return ResponseEntity.ok()
                             .body(userExerciseService.getAllUserExercisesByUserId(
                                     userId, pagingRequest, userExerciseFilterRequest));
    }

    @Override
    public ResponseEntity<UserExerciseResponse> getUserExerciseByUserIdAndExerciseName(Long userId, String name) {
        log.info("Getting user exercise with name {} for user {}", name, userId);
        return ResponseEntity.ok().body(userExerciseService.getUserExerciseByUserIdAndExerciseName(userId, name));
    }
}
