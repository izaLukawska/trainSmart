package org.lukawska.trainsmart.trainingplan.application.preparation.processors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObjects.MuscleGroup;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.domain.entities.UserExercise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import({TestFixtures.class, PostgresTestConfig.class})
@Transactional
class EnableExerciseProcessorIT {

    @Autowired
    private TestFixtures testFixtures;

    @Autowired
    private EnableExerciseProcessor enableExerciseProcessor;

    private User user;

    private List<UserExercise> userExercises;

    @BeforeEach
    void setUp() {
        user = testFixtures.user().save();
        userExercises = testFixtures.setUpUserExercises(user);
    }

    @Test
    void shouldReturnUserExerciseByMuscleGroupMapWhenEnableUserExercises() {
        //given
        final Long userId = user.getId();

        //when
        Map<MuscleGroup, List<UserExercise>> result = enableExerciseProcessor.enableUserExercises(userId,
                                                                                                  Optional.empty());

        //then
        assertThat(result.keySet()).contains(userExercises.getFirst().getExercise().getMuscleGroup());
        assertThat(result.keySet()).contains(userExercises.get(1).getExercise().getMuscleGroup());
        assertThat(result.keySet()).contains(userExercises.getLast().getExercise().getMuscleGroup());
    }
}
