package org.lukawska.trainsmart.healthsurvey.testutil;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyCreateRequest;
import org.lukawska.trainsmart.healthsurvey.domain.entites.HealthSurvey;
import org.lukawska.trainsmart.healthsurvey.domain.valueObjects.Gender;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;

import java.util.Set;

import static org.mockito.Mockito.mock;

@UtilityClass
public class HealthSurveyTestData {

    public static HealthSurveyCreateRequest healthSurveyRequest() {
        return new HealthSurveyCreateRequest(Gender.FEMALE, 180, 60, defaultInjuries());
    }

    public static HealthSurvey healthSurveyEntity() {
        return HealthSurvey.builder()
                           .user(mock(User.class))
                           .gender(Gender.MALE)
                           .height(180)
                           .weight(80)
                           .injuries(defaultInjuries())
                           .build();
    }

    public Set<String> defaultInjuries() {
        return Set.of("sprained ankle", "wrist pain", "dislocated arm");
    }
}
