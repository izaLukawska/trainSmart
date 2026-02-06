package org.lukawska.trainsmart.trainingplan.domain.repository;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.persistence.JpaConfig;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingPlan;
import org.lukawska.trainsmart.trainingplan.domain.repositories.TrainingPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestFixtures.class, PostgresTestConfig.class, JpaConfig.class})
class TrainingPlanRepositoryIT {

    @Autowired
    private TrainingPlanRepository trainingPlanRepository;

    @Autowired
    private TestFixtures testFixtures;

    @Test
    void shouldReturnTrainingPlanWhenMaxCreatedAtByUserIdPresent() {
        //given
        final TrainingPlan trainingPlan = testFixtures.trainingPlan().save();
        final Long userId = trainingPlan.getUser().getId();

        //when
        Optional<Instant> result = trainingPlanRepository.findMaxCreatedAtByUserId(userId);

        //then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(trainingPlan.getCreatedAt());
    }

    @Test
    void shouldReturnOptionalEmptyWhenMaxCreatedAtByUserIdNotPresent() {
        //when
        Optional<Instant> result = trainingPlanRepository.findMaxCreatedAtByUserId(2L);

        //then
        assertThat(result).isEmpty();
    }
}
