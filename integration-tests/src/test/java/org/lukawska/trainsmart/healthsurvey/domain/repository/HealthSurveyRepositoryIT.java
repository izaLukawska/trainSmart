package org.lukawska.trainsmart.healthsurvey.domain.repository;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.healthsurvey.domain.entites.HealthSurvey;
import org.lukawska.trainsmart.healthsurvey.domain.repositories.HealthSurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({PostgresTestConfig.class, TestFixtures.class})
@ActiveProfiles("test")
class HealthSurveyRepositoryIT {

    @Autowired
    private TestFixtures testFixtures;

    @Autowired
    private HealthSurveyRepository healthSurveyRepository;

    @Test
    void shouldReturnAllInjuriesByUserId() {
        //given
        final HealthSurvey healthSurvey = testFixtures.healthSurvey().save();

        //when
        Optional<Set<String>> actualResult = healthSurveyRepository.findAllInjuriesByUserId(
                healthSurvey.getUser().getId());

        //then
        assertThat(actualResult.isPresent()).isTrue();
        assertThat(actualResult.get()).isEqualTo(healthSurvey.getInjuries());
    }

    @Test
    void shouldThrowExceptionWhenConstraintViolation() {
        //given
        final HealthSurvey healthSurvey = testFixtures.healthSurvey().build();
        final HealthSurvey duplicate = testFixtures.healthSurvey()
                                                   .forUser(healthSurvey.getUser())
                                                   .build();
        healthSurveyRepository.saveAndFlush(healthSurvey);

        //when && then
        assertThatThrownBy(() -> healthSurveyRepository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
