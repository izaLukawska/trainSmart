package org.lukawska.trainsmart.trainingplan.application.preparation.processors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.exercisecatalog.domain.entities.Exercise;
import org.lukawska.trainsmart.openai.infra.adapter.OpenAiAdapter;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.domain.entities.UserExercise;
import org.lukawska.trainsmart.trainingplan.domain.repositories.UserExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Import({TestFixtures.class, PostgresTestConfig.class})
@Transactional
class UserExerciseFilterIT {

    @Autowired
    private TestFixtures testFixtures;

    @Autowired
    private UserExerciseFilter userExerciseFilter;

    @Autowired
    private UserExerciseRepository userExerciseRepository;

    @MockitoBean
    private OpenAiAdapter openAiAdapter;

    private User user;

    private List<UserExercise> userExercises;

    @BeforeEach
    void setUp() {
        user = testFixtures.user().save();
        userExercises = testFixtures.setUpUserExercises(user);
    }

    @Test
    void shouldUpdateUnsafeExercisesSuccess() {
        //given
        final Long userId = user.getId();
        final Set<String> injuries = Set.of("injury");
        when(openAiAdapter.sendPrompt(any())).thenReturn(userExercises.getLast().getExercise().getName());
        List<String> exerciseNames = userExercises.stream().map(UserExercise::getExercise)
                                                  .map(Exercise::getName)
                                                  .toList();

        //when
        userExerciseFilter.updateUnsafeExercises(userId, injuries, exerciseNames);

        //then
        List<UserExercise> updatedExercises = userExerciseRepository.findAllByUserId(userId);
        assertThat(updatedExercises.getFirst().isEnabled()).isTrue();
        assertThat(updatedExercises.get(1).isEnabled()).isTrue();
        assertThat(updatedExercises.getLast().isEnabled()).isFalse();
    }
}
