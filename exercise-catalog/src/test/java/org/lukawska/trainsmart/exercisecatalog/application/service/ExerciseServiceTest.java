package org.lukawska.trainsmart.exercisecatalog.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.exercisecatalog.application.exception.ExceptionType;
import org.lukawska.trainsmart.exercisecatalog.application.exception.ExerciseException;
import org.lukawska.trainsmart.exercisecatalog.domain.entities.Exercise;
import org.lukawska.trainsmart.exercisecatalog.domain.repositories.ExerciseRepository;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObjects.ExerciseType;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObjects.MuscleGroup;
import org.lukawska.trainsmart.exercisecatalog.model.ExerciseRequest;
import org.lukawska.trainsmart.exercisecatalog.model.ExerciseResponse;
import org.lukawska.trainsmart.exercisecatalog.model.ExerciseTypeEnum;
import org.lukawska.trainsmart.exercisecatalog.model.MuscleGroupEnum;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private ExerciseService exerciseService;

    @Test
    void shouldCreateExercisesSuccess() {
        //given
        final ExerciseRequest request = exerciseRequest();

        //when
        ExerciseResponse result = exerciseService.createExercise(request);

        //then
        assertThat(result.getName()).isEqualTo(request.getName());
    }

    @Test
    void shouldReturnExerciseByName() {
        //given
        final Exercise exercise = quadsBodyWeightExercise();
        when(exerciseRepository.findByName(exercise.getName())).thenReturn(Optional.of(exercise));

        //when
        ExerciseResponse result = exerciseService.getExerciseByName(exercise.getName());

        //then
        assertThat(result.getName()).isEqualTo(exercise.getName());
    }

    @Test
    void shouldReturnExercisesByMuscleGroup() {
        //given
        final Exercise exercise1 = quadsBodyWeightExercise();
        final Exercise exercise2 = quadsBodyWeightExercise();
        final MuscleGroup muscleGroup = exercise2.getMuscleGroup();
        final MuscleGroupEnum muscleGroupEnum = MuscleGroupEnum.valueOf(muscleGroup.name());
        when(exerciseRepository.findAllByMuscleGroup(muscleGroup)).thenReturn(List.of(exercise1, exercise2));

        //when
        List<ExerciseResponse> result = exerciseService.getExercisesByMuscleGroup(muscleGroupEnum);

        //then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ExerciseResponse::getName)
                          .containsExactlyInAnyOrder(exercise1.getName(), exercise2.getName());
    }

    @Test
    void shouldReturnAllExercises() {
        //given
        final Exercise exercise1 = quadsBodyWeightExercise();
        final Exercise exercise2 = quadsBodyWeightExercise();
        when(exerciseRepository.findAll()).thenReturn(List.of(exercise1, exercise2));

        //when

        List<Exercise> result = exerciseService.getAllExercises();

        //then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Exercise::getName)
                          .containsExactlyInAnyOrder(exercise1.getName(), exercise2.getName());

    }

    @Test
    void shouldReturnExercisesCreatedAfterOrEqualDate() {
        //given
        final Instant date = Instant.now();
        final Exercise exercise1 = quadsBodyWeightExercise();
        final Exercise exercise2 = quadsBodyWeightExercise();
        when(exerciseRepository.findAllByCreatedAtGreaterThanEqual(date)).thenReturn(List.of(exercise1, exercise2));

        //when
        List<Exercise> result = exerciseService.getExercisesByCreatedAtSince(date);

        //then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Exercise::getName)
                          .containsExactlyInAnyOrder(exercise1.getName(), exercise2.getName());
    }

    @Test
    void shouldReturnExercisesByExerciseType() {
        //given
        final Exercise exercise1 = quadsBodyWeightExercise();
        final Exercise exercise2 = quadsBodyWeightExercise();
        final ExerciseType exerciseType = exercise1.getExerciseType();
        when(exerciseRepository.findAllByExerciseType(exerciseType)).thenReturn(List.of(exercise1, exercise2));

        //when
        List<Exercise> result = exerciseService.getExercisesByType(exerciseType);

        //then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Exercise::getName)
                          .containsExactlyInAnyOrder(exercise1.getName(), exercise2.getName());
    }

    @Test
    void shouldThrowExerciseAlreadyExistsExceptionWhenCreateExercise() {
        //given
        final ExerciseRequest request = exerciseRequest();
        when(exerciseRepository.save(any())).thenThrow(new DataIntegrityViolationException("Uniqueness violation"));

        //when && then
        assertThatThrownBy(() -> exerciseService.createExercise(request))
                .isInstanceOf(ExerciseException.class)
                .hasMessage(ExceptionType.EXERCISE_ALREADY_EXISTS.getMessage());
    }

    @Test
    void shouldThrowExerciseNotFoundExceptionWhenGetExerciseByName() {
        //given
        final String invalidName = randomExerciseName();
        when(exerciseRepository.findByName(invalidName)).thenReturn(Optional.empty());

        //when && then
        assertThatThrownBy(() -> exerciseService.getExerciseByName(invalidName))
                .isInstanceOf(ExerciseException.class)
                .hasMessage(ExceptionType.EXERCISE_NOT_FOUND.getMessage());
    }

    private Exercise quadsBodyWeightExercise() {
        return new Exercise(randomExerciseName(), MuscleGroup.QUADS, ExerciseType.BODYWEIGHT);
    }

    private ExerciseRequest exerciseRequest() {
        return new ExerciseRequest(randomExerciseName(), MuscleGroupEnum.QUADS, ExerciseTypeEnum.OTHER);
    }

    private String randomExerciseName() {
        return "exercise" + new Random().nextInt();
    }
}
