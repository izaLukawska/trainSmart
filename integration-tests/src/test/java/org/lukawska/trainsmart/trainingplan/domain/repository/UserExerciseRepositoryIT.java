package org.lukawska.trainsmart.trainingplan.domain.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.persistence.JpaConfig;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.domain.entities.UserExercise;
import org.lukawska.trainsmart.trainingplan.domain.repositories.UserExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DataJpaTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestFixtures.class, PostgresTestConfig.class, JpaConfig.class})
class UserExerciseRepositoryIT {

    @Autowired
    private UserExerciseRepository userExerciseRepository;

    @Autowired
    private TestFixtures testFixtures;

    private List<UserExercise> userExercises;

    private User user;

    @BeforeEach
    void setUp() {
        user = testFixtures.user().save();
        final UserExercise userExercise1 = testFixtures.userExercise().enabled(false).forUser(user).save();
        final UserExercise userExercise2 = testFixtures.userExercise().forUser(user).save();
        final UserExercise userExercise3 = testFixtures.userExercise().forUser(user).save();
        userExercises = List.of(userExercise1, userExercise2, userExercise3);
    }

    @Test
    void shouldReturnAllExerciseNamesByUserId() {
        //given
        final Long userId = user.getId();
        final String name1 = userExercises.getFirst().getExercise().getName();
        final String name2 = userExercises.get(1).getExercise().getName();
        final String name3 = userExercises.getLast().getExercise().getName();

        //when
        List<String> result = userExerciseRepository.findAllExerciseNamesByUserId(userId);

        //then
        assertThat(result.size()).isEqualTo(userExercises.size());
        assertThat(result).contains(name1);
        assertThat(result).contains(name2);
        assertThat(result).contains(name3);
    }

    @Test
    void shouldEnableAllUserExercisesByUserId() {
        //given
        final Instant currentDate = Instant.now();
        final Long userId = user.getId();

        //when
        userExerciseRepository.enableAllByUserId(userId, currentDate);

        //then
        List<UserExercise> updatedExercises = userExerciseRepository.findAllByUserId(userId);
        UserExercise userExercise1 = updatedExercises.getFirst();
        UserExercise userExercise2 = updatedExercises.get(1);
        UserExercise userExercise3 = updatedExercises.getLast();
        assertThat(userExercise1.isEnabled()).isTrue();
        assertThat(userExercise2.isEnabled()).isTrue();
        assertThat(userExercise3.isEnabled()).isTrue();
    }

    @Test
    void shouldUpdateEnabledStatus() {
        //given
        final List<String> disabledNames = List.of(userExercises.getFirst().getExercise().getName());
        final Instant currentDate = Instant.now();
        final Long userId = user.getId();

        //when
        userExerciseRepository.updateEnabledByUserIdAndNames(userId, disabledNames, currentDate);

        //then
        List<UserExercise> updatedExercises = userExerciseRepository.findAllByUserId(userId);
        UserExercise userExercise1 = updatedExercises.getFirst();
        UserExercise userExercise2 = updatedExercises.get(1);
        UserExercise userExercise3 = updatedExercises.getLast();
        assertThat(userExercise1.isEnabled()).isFalse();
        assertThat(userExercise1.getModifiedAt()).isCloseTo(currentDate, within(1, ChronoUnit.SECONDS));
        assertThat(userExercise2.isEnabled()).isTrue();
        assertThat(userExercise2.getModifiedAt()).isCloseTo(currentDate, within(1, ChronoUnit.SECONDS));
        assertThat(userExercise3.isEnabled()).isTrue();
    }
}
