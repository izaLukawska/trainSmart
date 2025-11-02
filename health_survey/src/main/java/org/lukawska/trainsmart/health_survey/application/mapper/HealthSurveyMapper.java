package org.lukawska.trainsmart.health_survey.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.health_survey.application.dto.HealthSurveyRequest;
import org.lukawska.trainsmart.health_survey.application.dto.HealthSurveyResponse;
import org.lukawska.trainsmart.health_survey.domain.entites.HealthSurvey;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;

@UtilityClass
public class HealthSurveyMapper {

    public static HealthSurvey mapToEntity(HealthSurveyRequest surveyRequest, User user) {
        return HealthSurvey.builder()
                           .user(user)
                           .gender(surveyRequest.gender())
                           .birthDate(surveyRequest.birthDate())
                           .weight(surveyRequest.weight())
                           .injuries(surveyRequest.injuries())
                           .build();
    }

    public static HealthSurveyResponse mapToHealthSurveyResponse(HealthSurvey healthSurvey) {
        return new HealthSurveyResponse(healthSurvey.getId(),
                                        healthSurvey.getUser().getId(),
                                        healthSurvey.getGender(),
                                        healthSurvey.getAge(),
                                        healthSurvey.getWeight());
    }
}
