package org.lukawska.trainsmart.testutils.builders;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingPlan;
import org.lukawska.trainsmart.trainingplan.domain.repositories.TrainingPlanRepository;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;

import java.util.Optional;

@RequiredArgsConstructor
public class TrainingPlanFixtureBuilder {

    private final TrainingPlanRepository trainingPlanRepository;

    private final TestFixtures testFixtures;

    private User user;

    private TrainingType trainingType = TrainingType.STRENGTH;

    private PlanDuration planDuration = PlanDuration.EIGHT_WEEKS;

    private int daysPerWeek = 4;

    public TrainingPlanFixtureBuilder forUser(User user) {
        this.user = user;
        return this;
    }

    public TrainingPlan build() {
        User user = Optional.ofNullable(this.user).orElseGet(() -> testFixtures.user().save());
        return new TrainingPlan(user, trainingType, planDuration, daysPerWeek);
    }

    public TrainingPlan save() {
        return trainingPlanRepository.save(build());
    }
}
