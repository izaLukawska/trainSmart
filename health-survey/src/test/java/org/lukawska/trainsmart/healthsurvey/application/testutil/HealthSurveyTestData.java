package org.lukawska.trainsmart.healthsurvey.application.testutil;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyRequest;
import org.lukawska.trainsmart.healthsurvey.domain.entites.HealthSurvey;
import org.lukawska.trainsmart.healthsurvey.domain.valueObjects.Gender;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.mock;

@UtilityClass
public class HealthSurveyTestData {

    public static HealthSurveyRequest healthSurveyRequest() {
        return new HealthSurveyRequest(new Random().nextLong(100),
                                       Gender.FEMALE,
                                       LocalDate.of(2000, 10, 10),
                                       60,
                                       List.of("sprained ankle", "wrist pain", "dislocated arm"));
    }

    public static HealthSurvey healthSurveyEntity() {
        return HealthSurvey.builder()
                           .user(mock(User.class))
                           .gender(Gender.MALE)
                           .weight(80)
                           .birthDate(LocalDate.of(1999, 8, 5))
                           .injuries(List.of("wrist pain", "dislocated finger", "scoliosis"))
                           .build();
    }
}
