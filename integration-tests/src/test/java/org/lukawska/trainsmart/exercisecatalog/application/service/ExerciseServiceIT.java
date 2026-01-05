package org.lukawska.trainsmart.exercisecatalog.application.service;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestBase;
import org.lukawska.trainsmart.exercisecatalog.application.dto.ExerciseRequest;
import org.lukawska.trainsmart.exercisecatalog.application.dto.ExerciseResponse;
import org.lukawska.trainsmart.exercisecatalog.application.exception.ExceptionType;
import org.lukawska.trainsmart.exercisecatalog.application.exception.ExerciseException;
import org.lukawska.trainsmart.exercisecatalog.domain.entity.Exercise;
import org.lukawska.trainsmart.exercisecatalog.domain.repository.ExerciseRepository;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.ExerciseType;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ExerciseServiceIT extends PostgresTestBase {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExerciseService exerciseService;

    static Exercise dumbbellShoulderExercise() {
        return new Exercise(UUID.randomUUID().toString(), MuscleGroup.SHOULDERS, ExerciseType.DUMBBELL);
    }

    @Test
    void shouldCreateExerciseSuccess() {
        //given
        final ExerciseRequest request = new ExerciseRequest("front squat", MuscleGroup.QUADS, ExerciseType.BARBELL);

        //when
        ExerciseResponse result = exerciseService.createExercise(request);

        //then
        Optional<Exercise> savedExercise = exerciseRepository.findById(result.id());
        Exercise exercise = savedExercise.get();
        assertThat(savedExercise).isPresent();
        assertThat(exercise.getName()).isEqualTo(result.name());
        assertThat(exercise.getExerciseType()).isEqualTo(request.exerciseType());
        assertThat(exercise.getMuscleGroup()).isEqualTo(request.muscleGroup());
        assertThat(exercise.getCreatedAt()).isNotNull();
        assertThat(exercise.getModifiedAt()).isNotNull();
    }

    @Test
    void shouldReturnExerciseByName() {
        //given
        final Exercise exercise = dumbbellShoulderExercise();
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
        final Exercise exercise1 = new Exercise(UUID.randomUUID().toString(), MuscleGroup.CHEST, ExerciseType.OTHER);
        final Exercise exercise2 = dumbbellShoulderExercise();
        final Exercise exercise3 = dumbbellShoulderExercise();
        final MuscleGroup muscleGroup = exercise2.getMuscleGroup();
        exerciseRepository.saveAll(List.of(exercise1, exercise2, exercise3));

        //when
        List<ExerciseResponse> result = exerciseService.getExercisesByMuscleGroup(muscleGroup);

        //then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ExerciseResponse::name)
                          .containsExactlyInAnyOrder(exercise2.getName(), exercise3.getName())
                          .doesNotContain(exercise1.getName());
    }

    @Test
    void shouldReturnAllExercises() {
        //given
        final Exercise exercise1 = new Exercise("walking lunges", MuscleGroup.QUADS, ExerciseType.DUMBBELL);
        final Exercise exercise2 = dumbbellShoulderExercise();
        final Exercise exercise3 = dumbbellShoulderExercise();
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
        final Exercise exercise1 = new Exercise("walking lunges", MuscleGroup.QUADS, ExerciseType.BARBELL);
        final Exercise exercise2 = dumbbellShoulderExercise();
        final Exercise exercise3 = dumbbellShoulderExercise();
        final ExerciseType exerciseType = exercise2.getExerciseType();
        exerciseRepository.saveAll(List.of(exercise1, exercise2, exercise3));

        //when
        List<Exercise> result = exerciseService.getExercisesByType(exerciseType);

        //then
        assertThat(result).hasSize(2);
        assertThat(result).doesNotContain(exercise1);
        assertThat(result).containsExactlyInAnyOrder(exercise2, exercise3);
    }

    @Test
    void shouldReturnExercisesCreatedAtGreaterOrEqual() {
        //given
        final Exercise exercise1 = new Exercise("walking lunges", MuscleGroup.QUADS, ExerciseType.DUMBBELL);
        final Exercise exercise2 = dumbbellShoulderExercise();
        final Exercise exercise3 = dumbbellShoulderExercise();

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
        final ExerciseRequest request = new ExerciseRequest("front squat", MuscleGroup.QUADS, ExerciseType.BARBELL);
        final Exercise exercise = new Exercise(request.name(), request.muscleGroup(), request.exerciseType());
        exerciseRepository.save(exercise);

        //when && then
        assertThatThrownBy(() -> exerciseService.createExercise(request))
                .isInstanceOf(ExerciseException.class)
                .hasMessage(ExceptionType.EXERCISE_ALREADY_EXISTS.getMessage());
    }
}
