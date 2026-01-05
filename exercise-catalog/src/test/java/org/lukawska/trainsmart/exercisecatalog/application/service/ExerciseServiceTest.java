package org.lukawska.trainsmart.exercisecatalog.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.exercisecatalog.application.dto.ExerciseRequest;
import org.lukawska.trainsmart.exercisecatalog.application.dto.ExerciseResponse;
import org.lukawska.trainsmart.exercisecatalog.application.exception.ExceptionType;
import org.lukawska.trainsmart.exercisecatalog.application.exception.ExerciseException;
import org.lukawska.trainsmart.exercisecatalog.domain.entity.Exercise;
import org.lukawska.trainsmart.exercisecatalog.domain.repository.ExerciseRepository;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.ExerciseType;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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
        final ExerciseRequest request = new ExerciseRequest("front squat", MuscleGroup.QUADS, ExerciseType.BARBELL);

        //when
        ExerciseResponse result = exerciseService.createExercise(request);

        //then
        assertThat(result.name()).isEqualTo(request.name());
    }

    @Test
    void shouldReturnExerciseByName() {
        //given
        final Exercise exercise = new Exercise("front squat", MuscleGroup.QUADS, ExerciseType.BARBELL);
        when(exerciseRepository.findByName(exercise.getName())).thenReturn(Optional.of(exercise));

        //when
        ExerciseResponse result = exerciseService.getExerciseByName(exercise.getName());

        //then
        assertThat(result.name()).isEqualTo(exercise.getName());
    }

    @Test
    void shouldReturnExercisesByMuscleGroup() {
        //given
        final MuscleGroup muscleGroup = MuscleGroup.BACK;
        final Exercise exercise1 = new Exercise("pull up", muscleGroup, ExerciseType.BODYWEIGHT);
        final Exercise exercise2 = new Exercise("chin up", muscleGroup, ExerciseType.BODYWEIGHT);
        when(exerciseRepository.findAllByMuscleGroup(muscleGroup)).thenReturn(List.of(exercise1, exercise2));

        //when
        List<ExerciseResponse> result = exerciseService.getExercisesByMuscleGroup(muscleGroup);

        //then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ExerciseResponse::name)
                          .containsExactlyInAnyOrder(exercise1.getName(), exercise2.getName());
    }

    @Test
    void shouldReturnAllExercises() {
        //given
        final Exercise exercise1 = new Exercise("pull up", MuscleGroup.BACK, ExerciseType.BODYWEIGHT);
        final Exercise exercise2 = new Exercise("front squat", MuscleGroup.QUADS, ExerciseType.BARBELL);
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
        final Exercise exercise1 = new Exercise("pull up", MuscleGroup.BACK, ExerciseType.BODYWEIGHT);
        final Exercise exercise2 = new Exercise("front squat", MuscleGroup.QUADS, ExerciseType.BARBELL);
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
        final ExerciseType exerciseType = ExerciseType.BODYWEIGHT;
        final Exercise exercise1 = new Exercise("air squat", MuscleGroup.QUADS, exerciseType);
        final Exercise exercise2 = new Exercise("chin up", MuscleGroup.BACK, exerciseType);
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
        final ExerciseRequest request = new ExerciseRequest("front squat", MuscleGroup.QUADS, ExerciseType.BARBELL);
        when(exerciseRepository.save(any())).thenThrow(new DataIntegrityViolationException("Uniqueness violation"));

        //when && then
        assertThatThrownBy(() -> exerciseService.createExercise(request))
                .isInstanceOf(ExerciseException.class)
                .hasMessage(ExceptionType.EXERCISE_ALREADY_EXISTS.getMessage());
    }

    @Test
    void shouldThrowExerciseNotFoundExceptionWhenGetExerciseByName() {
        //given
        final String invalidName = "invalid name";
        when(exerciseRepository.findByName(invalidName)).thenReturn(Optional.empty());

        //when && then
        assertThatThrownBy(() -> exerciseService.getExerciseByName(invalidName))
                .isInstanceOf(ExerciseException.class)
                .hasMessage(ExceptionType.EXERCISE_NOT_FOUND.getMessage());
    }
}
