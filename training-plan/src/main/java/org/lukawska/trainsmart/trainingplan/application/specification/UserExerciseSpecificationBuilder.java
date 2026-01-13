package org.lukawska.trainsmart.trainingplan.application.specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.lukawska.trainsmart.trainingplan.application.dto.request.UserExerciseFilterDto;
import org.lukawska.trainsmart.trainingplan.domain.entities.UserExercise;
import org.lukawska.trainsmart.trainingplan.domain.specification.UserExerciseSpecification;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserExerciseSpecificationBuilder {

    public static Specification<UserExercise> build(Long userId, UserExerciseFilterDto filter) {
        return Specification.allOf(UserExerciseSpecification.byUserId(userId))
                            .and(UserExerciseSpecification.isMuscleGroup(filter.muscleGroup()))
                            .and(UserExerciseSpecification.isExerciseType(filter.exerciseType()))
                            .and(UserExerciseSpecification.isEnabled(filter.enabled()));
    }
}
