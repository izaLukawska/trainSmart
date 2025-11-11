package org.lukawska.trainsmart.healthsurvey.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyCreateRequest;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyResponse;
import org.lukawska.trainsmart.healthsurvey.domain.entites.HealthSurvey;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;

@UtilityClass
public class HealthSurveyMapper {

    public static HealthSurvey mapToEntity(HealthSurveyCreateRequest surveyRequest, User user) {
        return HealthSurvey.builder()
                           .user(user)
                           .gender(surveyRequest.gender())
                           .height(surveyRequest.height())
                           .weight(surveyRequest.weight())
                           .injuries(surveyRequest.injuries())
                           .build();
    }

    public static HealthSurveyResponse mapToHealthSurveyResponse(HealthSurvey healthSurvey) {
        return new HealthSurveyResponse(healthSurvey.getId(),
                                        healthSurvey.getGender(),
                                        healthSurvey.getHeight(),
                                        healthSurvey.getWeight(),
                                        healthSurvey.getInjuries().size());
    }
}
