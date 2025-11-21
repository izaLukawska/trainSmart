package exercisecatalog.presentation.controller;

import exercisecatalog.application.dto.ExerciseRequest;
import exercisecatalog.application.dto.ExerciseResponse;
import exercisecatalog.application.service.ExerciseService;
import exercisecatalog.domain.valueObject.MuscleGroup;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exercises")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ExerciseController {

    private final ExerciseService exerciseService;

    @PostMapping
    public ResponseEntity<ExerciseResponse> createExercise(@RequestBody @Valid ExerciseRequest request) {
        log.info("Creating exercise with name: {}, muscle group: {}, type: {}",
                 request.name(), request.muscleGroup().name(), request.exerciseType().name());
        return ResponseEntity.status(HttpStatus.CREATED).body(exerciseService.createExercise(request));
    }

    @GetMapping("/name")
    public ExerciseResponse getExercise(@RequestParam("name") @NotBlank String name) {
        log.info("Getting exercise by name: {}", name);
        return exerciseService.getExercise(name);
    }

    @GetMapping("/muscle-group")
    public List<ExerciseResponse> getExercisesByMuscleGroup(
            @RequestParam("muscleGroup") @NotNull MuscleGroup muscleGroup) {
        return exerciseService.getExercisesByMuscleGroup(muscleGroup);
    }
}
