package org.lukawska.trainsmart.healthsurvey.testutil;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.healthsurvey.application.dto.CreateHealthSurveyRequest;
import org.lukawska.trainsmart.healthsurvey.domain.entites.HealthSurvey;
import org.lukawska.trainsmart.healthsurvey.domain.valueObjects.Gender;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;

import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.mock;

@UtilityClass
public class HealthSurveyTestData {

    public static CreateHealthSurveyRequest healthSurveyRequest() {
        final Long userId = new Random().nextLong(100);
        return new CreateHealthSurveyRequest(userId, Gender.FEMALE, 60, defaultInjuries());
    }

    public static HealthSurvey healthSurveyEntity() {
        return HealthSurvey.builder()
                           .user(mock(User.class))
                           .gender(Gender.MALE)
                           .weight(80)
                           .injuries(defaultInjuries())
                           .build();
    }

    private List<String> defaultInjuries() {
        return List.of("sprained ankle", "wrist pain", "dislocated arm");
    }
}
