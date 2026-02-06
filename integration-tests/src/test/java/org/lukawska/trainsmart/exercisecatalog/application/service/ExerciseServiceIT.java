package org.lukawska.trainsmart.exercisecatalog.application.service;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.config.TestFixtures;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lukawska.trainsmart.testutils.TestData.exerciseName;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import({PostgresTestConfig.class, TestFixtures.class})
class ExerciseServiceIT {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private TestFixtures testFixtures;

    @Test
    void shouldCreateExerciseSuccess() {
        //given
        final ExerciseRequest request = new ExerciseRequest(exerciseName(), MuscleGroupEnum.CHEST,
                                                            ExerciseTypeEnum.OTHER);

        //when
        ExerciseResponse result = exerciseService.createExercise(request);

        //then
        Optional<Exercise> savedExercise = exerciseRepository.findById(result.getId());
        assertThat(savedExercise).isPresent();
        Exercise exercise = savedExercise.get();
        assertThat(exercise.getName()).isEqualTo(result.getName());
        assertThat(exercise.getExerciseType().name()).isEqualTo(request.getExerciseType().name());
        assertThat(exercise.getMuscleGroup().name()).isEqualTo(request.getMuscleGroup().name());
        assertThat(exercise.getCreatedAt()).isNotNull();
        assertThat(exercise.getModifiedAt()).isNotNull();
    }

    @Test
    void shouldReturnExerciseByName() {
        //given
        final Exercise exercise = testFixtures.exercise().save();

        //when
        ExerciseResponse result = exerciseService.getExerciseByName(exercise.getName());

        //then
        assertThat(result.getName()).isEqualTo(exercise.getName());
        assertThat(result.getId()).isEqualTo(exercise.getId());
    }

    @Test
    void shouldReturnExercisesByMuscleGroup() {
        //given
        final Exercise exercise1 = testFixtures.exercise().save();
        final Exercise exercise2 = testFixtures.exercise().save();
        final Exercise exercise3 = testFixtures.exercise()
                                               .withMuscleGroup(MuscleGroup.HAMSTRINGS)
                                               .save();
        final MuscleGroup muscleGroup = exercise1.getMuscleGroup();
        final MuscleGroupEnum muscleGroupEnum = MuscleGroupEnum.valueOf(muscleGroup.name());

        //when
        List<ExerciseResponse> result = exerciseService.getExercisesByMuscleGroup(muscleGroupEnum);

        //then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ExerciseResponse::getName)
                          .containsExactlyInAnyOrder(exercise2.getName(), exercise1.getName())
                          .doesNotContain(exercise3.getName());
    }

    @Test
    void shouldReturnAllExercises() {
        //given
        final Exercise exercise1 = testFixtures.exercise().save();
        final Exercise exercise2 = testFixtures.exercise().save();
        final Exercise exercise3 = testFixtures.exercise().save();

        //when
        List<Exercise> result = exerciseService.getAllExercises();

        //then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrder(exercise1, exercise2, exercise3);
    }

    @Test
    void shouldReturnExercisesByType() {
        //given
        final Exercise exercise1 = testFixtures.exercise().save();
        final Exercise exercise2 = testFixtures.exercise().save();
        final Exercise exercise3 = testFixtures.exercise()
                                               .withExerciseType(ExerciseType.OTHER)
                                               .save();
        final ExerciseType exerciseType = exercise1.getExerciseType();

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
        final Exercise exercise1 = testFixtures.exercise().save();
        final Exercise exercise2 = testFixtures.exercise().save();
        final Exercise exercise3 = testFixtures.exercise().save();

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
        final Exercise exercise = testFixtures.exercise().save();
        final ExerciseRequest request = new ExerciseRequest(
                exercise.getName(), MuscleGroupEnum.valueOf(exercise.getMuscleGroup().name()),
                ExerciseTypeEnum.valueOf(exercise.getExerciseType().name()));

        //when && then
        assertThatThrownBy(() -> exerciseService.createExercise(request))
                .isInstanceOf(ExerciseException.class)
                .hasMessage(ExceptionType.EXERCISE_ALREADY_EXISTS.getMessage());
    }
}
