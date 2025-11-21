package exercisecatalog.domain.repository;

import exercisecatalog.domain.entity.Exercise;
import exercisecatalog.domain.valueObject.ExerciseType;
import exercisecatalog.domain.valueObject.MuscleGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    Optional<Exercise> findByName(String name);

    List<Exercise> findAllByCreatedAtGreaterThanEqual(Instant date);

    List<Exercise> findAllByExerciseType(ExerciseType exerciseType);

    List<Exercise> findAllByMuscleGroup(MuscleGroup muscleGroup);

}
