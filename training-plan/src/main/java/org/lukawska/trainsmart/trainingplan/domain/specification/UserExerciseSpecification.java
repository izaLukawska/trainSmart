package org.lukawska.trainsmart.trainingplan.domain.specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObjects.ExerciseType;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObjects.MuscleGroup;
import org.lukawska.trainsmart.trainingplan.domain.entities.UserExercise;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserExerciseSpecification {

    public static Specification<UserExercise> byUserId(Long userId) {
        return (root, query, criteriaBuilder) -> userId == null ?
                criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("user").get("id"), userId);
    }

    public static Specification<UserExercise> isMuscleGroup(MuscleGroup muscleGroup) {
        return ((root, query, criteriaBuilder) -> muscleGroup == null ?
                criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("exercise").get("muscleGroup"),
                                                                      muscleGroup));
    }

    public static Specification<UserExercise> isExerciseType(ExerciseType exerciseType) {
        return ((root, query, criteriaBuilder) -> exerciseType == null ?
                criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("exercise")
                                                                          .get("exerciseType"), exerciseType));
    }

    public static Specification<UserExercise> isEnabled(Boolean enabled) {
        return ((root, query, criteriaBuilder) -> enabled == null ?
                criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("enabled"), enabled));
    }
}
