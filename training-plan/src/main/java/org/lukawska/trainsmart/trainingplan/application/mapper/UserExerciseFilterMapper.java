package org.lukawska.trainsmart.trainingplan.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObjects.ExerciseType;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObjects.MuscleGroup;
import org.lukawska.trainsmart.trainingplan.application.dto.UserExerciseFilterDto;
import org.lukawska.trainsmart.trainingplan.model.ExerciseTypeEnum;
import org.lukawska.trainsmart.trainingplan.model.MuscleGroupEnum;
import org.lukawska.trainsmart.trainingplan.model.UserExerciseFilterRequest;

@UtilityClass
public class UserExerciseFilterMapper {

    public static UserExerciseFilterDto mapToUserExerciseFilterDto(UserExerciseFilterRequest filterRequest) {
        MuscleGroup muscleGroup = resolveMuscleGroupValue(filterRequest.getMuscleGroup());
        ExerciseType exerciseType = resolveExerciseTypeValue(filterRequest.getExerciseType());
        return UserExerciseFilterDto.builder()
                                    .enabled(filterRequest.getEnabled())
                                    .muscleGroup(muscleGroup)
                                    .exerciseType(exerciseType)
                                    .build();
    }

    private MuscleGroup resolveMuscleGroupValue(MuscleGroupEnum muscleGroupEnum) {
        return muscleGroupEnum == null ? null : MuscleGroup.valueOf(muscleGroupEnum.name());
    }

    private ExerciseType resolveExerciseTypeValue(ExerciseTypeEnum exerciseTypeEnum) {
        return exerciseTypeEnum == null ? null : ExerciseType.valueOf(exerciseTypeEnum.name());
    }
}
