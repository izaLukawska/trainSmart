package org.lukawska.trainsmart.exercisecatalog.application.service;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.exercisecatalog.application.dto.ExerciseRequest;
import org.lukawska.trainsmart.exercisecatalog.application.dto.ExerciseResponse;
import org.lukawska.trainsmart.exercisecatalog.application.exception.ExceptionType;
import org.lukawska.trainsmart.exercisecatalog.application.exception.ExerciseException;
import org.lukawska.trainsmart.exercisecatalog.domain.entities.Exercise;
import org.lukawska.trainsmart.exercisecatalog.domain.repositories.ExerciseRepository;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObjects.ExerciseType;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObjects.MuscleGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lukawska.trainsmart.exercisecatalog.testutil.ExerciseTestData.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import(PostgresTestConfig.class)
class ExerciseServiceIT {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExerciseService exerciseService;

    @Test
    void shouldCreateExerciseSuccess() {
        //given
        final ExerciseRequest request = randomQuadsDumbbellExerciseRequest();

        //when
        ExerciseResponse result = exerciseService.createExercise(request);

        //then
        Optional<Exercise> savedExercise = exerciseRepository.findById(result.id());
        assertThat(savedExercise).isPresent();
        Exercise exercise = savedExercise.get();
        assertThat(exercise.getName()).isEqualTo(result.name());
        assertThat(exercise.getExerciseType()).isEqualTo(request.exerciseType());
        assertThat(exercise.getMuscleGroup()).isEqualTo(request.muscleGroup());
        assertThat(exercise.getCreatedAt()).isNotNull();
        assertThat(exercise.getModifiedAt()).isNotNull();
    }

    @Test
    void shouldReturnExerciseByName() {
        //given
        final Exercise exercise = randomQuadsDumbbellExercise();
        exerciseRepository.save(exercise);

        //when
        ExerciseResponse result = exerciseService.getExerciseByName(exercise.getName());

        //then
        assertThat(result.name()).isEqualTo(exercise.getName());
        assertThat(result.id()).isEqualTo(exercise.getId());
    }

    @Test
    void shouldReturnExercisesByMuscleGroup() {
        //given
        final Exercise exercise1 = randomQuadsDumbbellExercise();
        final Exercise exercise2 = randomQuadsDumbbellExercise();
        final Exercise exercise3 = new Exercise(randomExerciseName(), MuscleGroup.ABS, ExerciseType.DUMBBELL);
        final MuscleGroup muscleGroup = exercise1.getMuscleGroup();
        exerciseRepository.saveAll(List.of(exercise1, exercise2, exercise3));

        //when
        List<ExerciseResponse> result = exerciseService.getExercisesByMuscleGroup(muscleGroup);

        //then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ExerciseResponse::name)
                          .containsExactlyInAnyOrder(exercise2.getName(), exercise1.getName())
                          .doesNotContain(exercise3.getName());
    }

    @Test
    void shouldReturnAllExercises() {
        //given
        final Exercise exercise1 = randomQuadsDumbbellExercise();
        final Exercise exercise2 = randomQuadsDumbbellExercise();
        final Exercise exercise3 = new Exercise(randomExerciseName(), MuscleGroup.SHOULDERS, ExerciseType.BARBELL);
        exerciseRepository.saveAll(List.of(exercise1, exercise2, exercise3));

        //when
        List<Exercise> result = exerciseService.getAllExercises();

        //then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrder(exercise1, exercise2, exercise3);
    }

    @Test
    void shouldReturnExercisesByType() {
        //given
        final Exercise exercise1 = randomQuadsDumbbellExercise();
        final Exercise exercise2 = randomQuadsDumbbellExercise();
        final Exercise exercise3 = new Exercise(randomExerciseName(), MuscleGroup.SHOULDERS, ExerciseType.BARBELL);
        final ExerciseType exerciseType = exercise1.getExerciseType();
        exerciseRepository.saveAll(List.of(exercise1, exercise2, exercise3));

        //when
        List<Exercise> result = exerciseService.getExercisesByType(exerciseType);

        //then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(exercise1, exercise2);
        assertThat(result).doesNotContain(exercise3);
    }

    @Test
    void shouldReturnExercisesCreatedAtGreaterOrEqual() {
        //given
        final Exercise exercise1 = randomQuadsDumbbellExercise();
        final Exercise exercise2 = randomQuadsDumbbellExercise();
        final Exercise exercise3 = randomQuadsDumbbellExercise();

        exerciseRepository.save(exercise1);
        exerciseRepository.saveAll(List.of(exercise2, exercise3));

        //when
        List<Exercise> result = exerciseService.getExercisesByCreatedAtSince(exercise2.getCreatedAt());

        //then
        assertThat(result).hasSize(2);
        assertThat(result).doesNotContain(exercise1);
        assertThat(result).containsExactlyInAnyOrder(exercise2, exercise3);
    }

    @Test
    void shouldThrowDataIntegrityViolationExceptionWhenCreateExerciseAlreadyExists() {
        //given
        final ExerciseRequest request = randomQuadsDumbbellExerciseRequest();
        final Exercise exercise = new Exercise(request.name(), request.muscleGroup(), request.exerciseType());
        exerciseRepository.save(exercise);

        //when && then
        assertThatThrownBy(() -> exerciseService.createExercise(request))
                .isInstanceOf(ExerciseException.class)
                .hasMessage(ExceptionType.EXERCISE_ALREADY_EXISTS.getMessage());
    }
}
