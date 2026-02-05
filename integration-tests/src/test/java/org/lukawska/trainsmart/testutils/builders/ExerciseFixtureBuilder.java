package org.lukawska.trainsmart.testutils.builders;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.exercisecatalog.domain.entities.Exercise;
import org.lukawska.trainsmart.exercisecatalog.domain.repositories.ExerciseRepository;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObjects.ExerciseType;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObjects.MuscleGroup;

import java.util.UUID;

@RequiredArgsConstructor
public class ExerciseFixtureBuilder {

    private final ExerciseRepository exerciseRepository;

    private String name = UUID.randomUUID().toString();

    private MuscleGroup muscleGroup = MuscleGroup.QUADS;

    private ExerciseType exerciseType = ExerciseType.BARBELL;

    public ExerciseFixtureBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ExerciseFixtureBuilder withMuscleGroup(MuscleGroup muscleGroup) {
        this.muscleGroup = muscleGroup;
        return this;
    }

    public ExerciseFixtureBuilder withExerciseType(ExerciseType exerciseType) {
        this.exerciseType = exerciseType;
        return this;
    }

    public Exercise build() {
        return new Exercise(name, muscleGroup, exerciseType);
    }

    public Exercise save() {
        return exerciseRepository.save(build());
    }
}
