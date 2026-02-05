package org.lukawska.trainsmart.exercisecatalog.domain.repositories;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.exercisecatalog.domain.entities.Exercise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import({PostgresTestConfig.class, TestFixtures.class})
class ExerciseRepositoryIT {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private TestFixtures testFixtures;

    @Test
    void shouldThrowExceptionWhenConstraintViolation() {
        //given
        final Exercise exercise = testFixtures.exercise().save();
        final Exercise duplicate = testFixtures.exercise()
                                               .withName(exercise.getName())
                                               .build();

        //when && then
        assertThatThrownBy(() -> exerciseRepository.save(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
