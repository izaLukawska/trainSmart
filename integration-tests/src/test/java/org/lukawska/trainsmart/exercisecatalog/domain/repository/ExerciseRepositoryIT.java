package org.lukawska.trainsmart.exercisecatalog.domain.repository;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestBase;
import org.lukawska.trainsmart.exercisecatalog.domain.entity.Exercise;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.ExerciseType;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ExerciseRepositoryIT extends PostgresTestBase {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Test
    void shouldThrowExceptionWhenConstraintViolation() {
        //given
        final Exercise exercise = new Exercise("back squat", MuscleGroup.QUADS, ExerciseType.BARBELL);
        exerciseRepository.saveAndFlush(exercise);
        final Exercise duplicate = new Exercise(exercise.getName(), MuscleGroup.BACK, ExerciseType.BARBELL);

        //when && then
        assertThatThrownBy(() -> exerciseRepository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
