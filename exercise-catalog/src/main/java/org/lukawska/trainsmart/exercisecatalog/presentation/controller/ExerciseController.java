package org.lukawska.trainsmart.exercisecatalog.presentation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.exercisecatalog.api.ExerciseApi;
import org.lukawska.trainsmart.exercisecatalog.application.service.ExerciseService;
import org.lukawska.trainsmart.exercisecatalog.model.ExerciseRequest;
import org.lukawska.trainsmart.exercisecatalog.model.ExerciseResponse;
import org.lukawska.trainsmart.exercisecatalog.model.MuscleGroupEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class ExerciseController implements ExerciseApi {

    private final ExerciseService exerciseService;

    @Override
    public ResponseEntity<ExerciseResponse> createExercise(ExerciseRequest exerciseRequest) {
        log.info("Received create exercise request for name: {}", exerciseRequest.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(exerciseService.createExercise(exerciseRequest));
    }

    @Override
    public ResponseEntity<ExerciseResponse> getExerciseByName(String name) {
        log.info("Received get exercise request for name: {}", name);
        return ResponseEntity.ok(exerciseService.getExerciseByName(name));
    }

    @Override
    public ResponseEntity<List<ExerciseResponse>> getExercisesByMuscleGroup(MuscleGroupEnum muscleGroup) {
        log.info("Received get exercise by muscle group request");
        return ResponseEntity.ok(exerciseService.getExercisesByMuscleGroup(muscleGroup));
    }
}
