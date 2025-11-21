package org.lukawska.trainsmart.exercisecatalog.application.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.exercisecatalog.application.dto.ExerciseRequest;
import org.lukawska.trainsmart.exercisecatalog.application.dto.ExerciseResponse;
import org.lukawska.trainsmart.exercisecatalog.application.exception.ExceptionType;
import org.lukawska.trainsmart.exercisecatalog.application.exception.ExerciseException;
import org.lukawska.trainsmart.exercisecatalog.application.mapper.ExerciseMapper;
import org.lukawska.trainsmart.exercisecatalog.domain.entity.Exercise;
import org.lukawska.trainsmart.exercisecatalog.domain.repository.ExerciseRepository;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.ExerciseType;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.List;

import static org.lukawska.trainsmart.exercisecatalog.application.mapper.ExerciseMapper.mapToExercise;
import static org.lukawska.trainsmart.exercisecatalog.application.mapper.ExerciseMapper.mapToResponse;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    @Transactional
    public ExerciseResponse createExercise(ExerciseRequest request) {
        try {
            Exercise exercise = mapToExercise(request);
            exerciseRepository.save(exercise);
            log.info("Saved exercise: {} with ID: {}", exercise.getName(), exercise.getId());
            return mapToResponse(exercise);
        } catch (DataIntegrityViolationException e) {
            log.warn("Failed to create exercise {} due to {}", request.name(), e.getMessage());
            throw new ExerciseException(ExceptionType.EXERCISE_ALREADY_EXISTS);
        }
    }

    public ExerciseResponse getExercise(@NotBlank String name) {
        Exercise foundExercise = exerciseRepository.findByName(name).orElseThrow(
                () -> new ExerciseException(ExceptionType.EXERCISE_NOT_FOUND));

        log.info("Found exercise with ID: {}", foundExercise.getId());

        return mapToResponse(foundExercise);
    }

    public List<ExerciseResponse> getExercisesByMuscleGroup(@NotNull MuscleGroup muscleGroup) {
        List<Exercise> exercises = exerciseRepository.findAllByMuscleGroup(muscleGroup);
        log.info("Found {} exercises for muscle group: {}", exercises.size(), muscleGroup.name());

        return exercises.stream()
                        .map(ExerciseMapper::mapToResponse)
                        .toList();
    }

    public List<Exercise> getAllExercises() {
        List<Exercise> exercises = exerciseRepository.findAll();
        log.info("Found {} exercises.", exercises.size());

        return exercises;
    }

    public List<Exercise> getExercisesFrom(@NotNull Instant date) {
        List<Exercise> exercises = exerciseRepository.findAllByCreatedAtGreaterThanEqual(date);
        log.info("Found {} exercises created after: {}", exercises.size(), date);

        return exercises;
    }

    public List<Exercise> getExercisesByType(@NotNull ExerciseType exerciseType) {
        List<Exercise> exercises = exerciseRepository.findAllByExerciseType(exerciseType);
        log.info("Found {} exercises for type: {}", exercises.size(), exerciseType.name());

        return exercises;
    }
}
