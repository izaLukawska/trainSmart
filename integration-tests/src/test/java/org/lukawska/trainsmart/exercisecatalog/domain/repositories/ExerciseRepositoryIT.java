package org.lukawska.trainsmart.exercisecatalog.domain.repositories;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.exercisecatalog.domain.entities.Exercise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lukawska.trainsmart.exercisecatalog.testutil.ExerciseTestData.randomQuadsDumbbellExercise;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(PostgresTestConfig.class)
class ExerciseRepositoryIT {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Test
    void shouldThrowExceptionWhenConstraintViolation() {
        //given
        final Exercise exercise = randomQuadsDumbbellExercise();
        exerciseRepository.saveAndFlush(exercise);
        final Exercise duplicate = new Exercise(
                exercise.getName(), exercise.getMuscleGroup(), exercise.getExerciseType());

        //when && then
        assertThatThrownBy(() -> exerciseRepository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
