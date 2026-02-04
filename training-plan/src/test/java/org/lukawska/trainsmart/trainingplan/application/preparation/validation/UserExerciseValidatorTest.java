package org.lukawska.trainsmart.trainingplan.application.preparation.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObjects.MuscleGroup;
import org.lukawska.trainsmart.trainingplan.application.exception.ExceptionType;
import org.lukawska.trainsmart.trainingplan.application.exception.TrainingPlanException;
import org.lukawska.trainsmart.trainingplan.domain.entities.UserExercise;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.lukawska.trainsmart.trainingplan.testutil.TrainingPlanTestData.validMuscleGroups;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UserExerciseValidatorTest {

    @Test
    void shouldNotThrowExceptionWhenValidData() {
        //given
        final Map<MuscleGroup, List<UserExercise>> groups = validMuscleGroups();

        //when && then
        assertThatCode(() -> UserExerciseValidator.validateUserExercises(groups)).doesNotThrowAnyException();
    }

    @Test
    void shouldThrowExceptionWhenNotEnoughMuscleGroups() {
        //given
        final Map<MuscleGroup, List<UserExercise>> groups = Map.of(MuscleGroup.CHEST,
                                                                   List.of(mock(UserExercise.class)));

        //when && then
        assertThatThrownBy(() -> UserExerciseValidator.validateUserExercises(groups))
                .isInstanceOf(TrainingPlanException.class)
                .hasMessageContaining(ExceptionType.NOT_ENOUGH_MUSCLE_GROUPS.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAnyMuscleGroupHasNoExercises() {
        final Map<MuscleGroup, List<UserExercise>> groups = Map.of(
                MuscleGroup.CHEST, List.of(), MuscleGroup.BACK, List.of(), MuscleGroup.ABS, List.of());

        //when && then
        assertThatThrownBy(() -> UserExerciseValidator.validateUserExercises(groups))
                .isInstanceOf(TrainingPlanException.class)
                .hasMessageContaining(ExceptionType.NOT_ENOUGH_EXERCISES.getMessage());
    }
}
