package org.lukawska.trainsmart.trainingplan.testutil;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanDto;
import org.lukawska.trainsmart.trainingplan.application.preparation.dto.TrainingPlanGenerationData;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingPlan;
import org.lukawska.trainsmart.trainingplan.domain.entities.UserExercise;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.WeekDay;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;

@UtilityClass
public class TrainingPlanTestData {

    public static Map<MuscleGroup, List<UserExercise>> validMuscleGroups() {
        return Map.of(MuscleGroup.BACK, List.of(mock(UserExercise.class)),
                      MuscleGroup.QUADS, List.of(mock(UserExercise.class)),
                      MuscleGroup.CHEST, List.of(mock(UserExercise.class)));
    }

    public static TrainingPlanGenerationData generationData() {
        return new TrainingPlanGenerationData(mock(User.class), validMuscleGroups(), TrainingType.STRENGTH,
                                              PlanDuration.FOUR_WEEKS, List.of(WeekDay.MONDAY));
    }

    public static TrainingPlanDto trainingPlanRequest() {
        return new TrainingPlanDto(TrainingType.STRENGTH, PlanDuration.FOUR_WEEKS, 3, List.of());
    }

    public static TrainingPlan trainingPlan(User user) {
        return new TrainingPlan(user, TrainingType.STRENGTH, PlanDuration.FOUR_WEEKS, 1);
    }
}
