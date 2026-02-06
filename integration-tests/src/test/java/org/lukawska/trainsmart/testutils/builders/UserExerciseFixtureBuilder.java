package org.lukawska.trainsmart.testutils.builders;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.exercisecatalog.domain.entities.Exercise;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.domain.entities.UserExercise;
import org.lukawska.trainsmart.trainingplan.domain.repositories.UserExerciseRepository;

import java.util.Optional;

@RequiredArgsConstructor
public class UserExerciseFixtureBuilder {

    private final UserExerciseRepository userExerciseRepository;

    private final TestFixtures testFixtures;

    private User user;

    private Exercise exercise;

    private boolean enabled = true;

    public UserExerciseFixtureBuilder withExercise(Exercise exercise) {
        this.exercise = exercise;
        return this;
    }

    public UserExerciseFixtureBuilder enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public UserExerciseFixtureBuilder forUser(User user) {
        this.user = user;
        return this;
    }

    public UserExercise build() {
        User user = Optional.ofNullable(this.user).orElseGet(() -> testFixtures.user().save());
        Exercise exercise = Optional.ofNullable(this.exercise).orElseGet(() -> testFixtures.exercise().save());
        UserExercise userExercise = new UserExercise(user, exercise);
        userExercise.setEnabled(enabled);
        return userExercise;
    }

    public UserExercise save() {
        return userExerciseRepository.save(build());
    }
}
