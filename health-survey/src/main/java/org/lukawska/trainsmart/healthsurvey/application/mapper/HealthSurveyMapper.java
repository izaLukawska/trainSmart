package org.lukawska.trainsmart.healthsurvey.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.healthsurvey.application.dto.CreateHealthSurveyRequest;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyResponse;
import org.lukawska.trainsmart.healthsurvey.domain.entites.HealthSurvey;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;

@UtilityClass
public class HealthSurveyMapper {

    public static HealthSurvey mapToEntity(CreateHealthSurveyRequest surveyRequest, User user) {
        return HealthSurvey.builder()
                           .user(user)
                           .gender(surveyRequest.gender())
                           .weight(surveyRequest.weight())
                           .injuries(surveyRequest.injuries())
                           .build();
    }

    public static HealthSurveyResponse mapToHealthSurveyResponse(HealthSurvey healthSurvey) {
        return new HealthSurveyResponse(healthSurvey.getId(),
                                        healthSurvey.getUser().getId(),
                                        healthSurvey.getGender(),
                                        healthSurvey.getWeight(),
                                        healthSurvey.getInjuries().size());
    }
}
