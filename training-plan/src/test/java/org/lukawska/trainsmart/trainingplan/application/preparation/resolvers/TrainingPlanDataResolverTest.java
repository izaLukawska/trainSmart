package org.lukawska.trainsmart.trainingplan.application.preparation.resolvers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanDto;
import org.lukawska.trainsmart.trainingplan.application.exception.ExceptionType;
import org.lukawska.trainsmart.trainingplan.application.exception.TrainingPlanException;
import org.lukawska.trainsmart.trainingplan.application.preparation.dto.TrainingPlanGenerationData;
import org.lukawska.trainsmart.trainingplan.application.preparation.processors.EnableExerciseProcessor;
import org.lukawska.trainsmart.trainingplan.domain.entities.UserExercise;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.WeekDay;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lukawska.trainsmart.trainingplan.testutil.TrainingPlanTestData.trainingPlanRequest;
import static org.lukawska.trainsmart.trainingplan.testutil.TrainingPlanTestData.validMuscleGroups;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingPlanDataResolverTest {

    @Mock
    private EnableExerciseProcessor enableExerciseProcessor;

    @InjectMocks
    private TrainingPlanDataResolver trainingPlanDataResolver;

    @ParameterizedTest
    @MethodSource("provideDaysPerWeek")
    void shouldReturnCorrectDefaultDays(int daysPerWeek, List<WeekDay> expectedResult) {
        // When
        final List<WeekDay> actualResult = trainingPlanDataResolver.resolvePreferredDays(List.of(), daysPerWeek);

        // Then
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void shouldReturnSortedDaysWhenPreferredDaysNotEmpty2() {
        //given
        final User user = mock(User.class);
        final TrainingPlanDto request = trainingPlanRequest();
        final Map<MuscleGroup, List<UserExercise>> groups = validMuscleGroups();

        when(enableExerciseProcessor.enableUserExercises(user.getId(), Optional.empty())).thenReturn(groups);

        //when
        TrainingPlanGenerationData result = trainingPlanDataResolver.getResolvedData(
                user, request, Optional.empty());

        //then
        assertThat(result.muscleGroups()).isEqualTo(groups);
    }

    @Test
    void shouldReturnSortedDaysWhenPreferredDaysNotEmpty() {
        //given
        final List<WeekDay> weekDays = List.of(WeekDay.FRIDAY, WeekDay.MONDAY);
        final List<WeekDay> expectedResult = List.of(WeekDay.MONDAY, WeekDay.FRIDAY);

        //when
        List<WeekDay> actualResult = trainingPlanDataResolver.resolvePreferredDays(weekDays, 2);

        //then
        assertThat(actualResult.size()).isEqualTo(2);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void shouldThrowNotEnoughMuscleGroupsWhenGetResolvedData() {
        //given
        final User user = mock(User.class);
        final TrainingPlanDto request = trainingPlanRequest();
        final Map<MuscleGroup, List<UserExercise>> groups = Map.of(MuscleGroup.ABS, List.of(mock(UserExercise.class)));

        when(enableExerciseProcessor.enableUserExercises(user.getId(), Optional.empty())).thenReturn(groups);

        //when && then
        assertThatThrownBy(() -> trainingPlanDataResolver.getResolvedData(user, request, Optional.empty()))
                .isInstanceOf(TrainingPlanException.class)
                .hasMessage(ExceptionType.NOT_ENOUGH_MUSCLE_GROUPS.getMessage());
    }

    @Test
    void shouldThrowNotEnoughUserExercisesWhenGetResolvedData() {
        //given
        final User user = mock(User.class);
        final TrainingPlanDto request = trainingPlanRequest();
        final Map<MuscleGroup, List<UserExercise>> groups = Map.of(
                MuscleGroup.ABS, List.of(mock(UserExercise.class)),
                MuscleGroup.QUADS, List.of(mock(UserExercise.class)),
                MuscleGroup.CHEST, List.of());

        when(enableExerciseProcessor.enableUserExercises(user.getId(), Optional.empty())).thenReturn(groups);

        //when && then
        assertThatThrownBy(() -> trainingPlanDataResolver.getResolvedData(user, request, Optional.empty()))
                .isInstanceOf(TrainingPlanException.class)
                .hasMessage(ExceptionType.NOT_ENOUGH_EXERCISES.getMessage());
    }

    private static Stream<Arguments> provideDaysPerWeek() {
        return Stream.of(
                Arguments.of(1, List.of(WeekDay.MONDAY)),
                Arguments.of(2, List.of(WeekDay.MONDAY, WeekDay.THURSDAY)),
                Arguments.of(3, List.of(WeekDay.MONDAY, WeekDay.WEDNESDAY, WeekDay.FRIDAY)),
                Arguments.of(4, List.of(WeekDay.MONDAY, WeekDay.TUESDAY, WeekDay.THURSDAY, WeekDay.FRIDAY)),
                Arguments.of(5, List.of(WeekDay.MONDAY, WeekDay.TUESDAY, WeekDay.WEDNESDAY,
                                        WeekDay.THURSDAY, WeekDay.FRIDAY)),
                Arguments.of(6, List.of(WeekDay.MONDAY, WeekDay.TUESDAY, WeekDay.WEDNESDAY,
                                        WeekDay.THURSDAY, WeekDay.FRIDAY, WeekDay.SATURDAY)),
                Arguments.of(7, Arrays.stream(WeekDay.values()).toList()));
    }
}
