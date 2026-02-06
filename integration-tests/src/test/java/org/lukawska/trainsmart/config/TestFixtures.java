package org.lukawska.trainsmart.config;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.exercisecatalog.domain.repositories.ExerciseRepository;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObjects.MuscleGroup;
import org.lukawska.trainsmart.healthsurvey.domain.repositories.HealthSurveyRepository;
import org.lukawska.trainsmart.mailing.domain.repositories.MailRepository;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.statements.domain.repositories.UserAgreementRepository;
import org.lukawska.trainsmart.testutils.builders.*;
import org.lukawska.trainsmart.trainingplan.domain.entities.UserExercise;
import org.lukawska.trainsmart.trainingplan.domain.repositories.TrainingPlanRepository;
import org.lukawska.trainsmart.trainingplan.domain.repositories.UserExerciseRepository;
import org.lukawska.trainsmart.usermanagement.domain.repository.RefreshTokenRepository;
import org.lukawska.trainsmart.usermanagement.domain.repository.UserRepository;
import org.lukawska.trainsmart.usermanagement.domain.repository.VerificationTokenRepository;
import org.springframework.boot.test.context.TestComponent;

import java.util.Arrays;
import java.util.List;

@TestComponent
@RequiredArgsConstructor
public class TestFixtures {

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final VerificationTokenRepository verificationTokenRepository;

    private final ExerciseRepository exerciseRepository;

    private final MailRepository mailRepository;

    private final UserAgreementRepository userAgreementRepository;

    private final HealthSurveyRepository healthSurveyRepository;

    private final TrainingPlanRepository trainingPlanRepository;

    private final UserExerciseRepository userExerciseRepository;

    public UserFixtureBuilder user() {
        return new UserFixtureBuilder(userRepository);
    }

    public RefreshTokenFixtureBuilder refreshToken() {
        return new RefreshTokenFixtureBuilder(refreshTokenRepository, this);
    }

    public VerificationTokenFixtureBuilder verificationToken() {
        return new VerificationTokenFixtureBuilder(verificationTokenRepository, this);
    }

    public ExerciseFixtureBuilder exercise() {
        return new ExerciseFixtureBuilder(exerciseRepository);
    }

    public MailingFixtureBuilder mail() {
        return new MailingFixtureBuilder(mailRepository);
    }

    public UserAgreementFixtureBuilder userAgreement() {
        return new UserAgreementFixtureBuilder(userAgreementRepository, this);
    }

    public HealthSurveyFixtureBuilder healthSurvey() {
        return new HealthSurveyFixtureBuilder(healthSurveyRepository, this);
    }

    public TrainingPlanFixtureBuilder trainingPlan() {
        return new TrainingPlanFixtureBuilder(trainingPlanRepository, this);
    }

    public UserExerciseFixtureBuilder userExercise() {
        return new UserExerciseFixtureBuilder(userExerciseRepository, this);
    }

    public List<UserExercise> setUpUserExercises(User user) {
        this.healthSurvey().forUser(user).save();
        return Arrays.stream(MuscleGroup.values())
                     .limit(4)
                     .map(group -> userExercise().withExercise(exercise().withMuscleGroup(group).save())
                                                 .forUser(user)
                                                 .save())
                     .toList();
    }
}
