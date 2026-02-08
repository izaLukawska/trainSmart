package org.lukawska.trainsmart.healthsurvey.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.healthsurvey.domain.entites.HealthSurvey;
import org.lukawska.trainsmart.healthsurvey.domain.valueObjects.Gender;
import org.lukawska.trainsmart.healthsurvey.model.GenderEnum;
import org.lukawska.trainsmart.healthsurvey.model.HealthSurveyCreateRequest;
import org.lukawska.trainsmart.healthsurvey.model.HealthSurveyResponse;
import org.lukawska.trainsmart.healthsurvey.model.WeightHistoryResponse;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.springframework.data.history.Revision;

import java.time.Instant;

@UtilityClass
public class HealthSurveyMapper {

    public static HealthSurvey mapToEntity(HealthSurveyCreateRequest surveyRequest, User user) {
        return HealthSurvey.builder()
                           .user(user)
                           .gender(Gender.valueOf(surveyRequest.getGender().name()))
                           .height(surveyRequest.getHeight())
                           .weight(surveyRequest.getWeight())
                           .injuries(surveyRequest.getInjuries())
                           .build();
    }

    public static HealthSurveyResponse mapToHealthSurveyResponse(HealthSurvey healthSurvey) {
        return new HealthSurveyResponse(healthSurvey.getId(),
                                        GenderEnum.valueOf(healthSurvey.getGender().name()),
                                        healthSurvey.getHeight(),
                                        healthSurvey.getWeight(),
                                        healthSurvey.getInjuries().size());
    }

    public static WeightHistoryResponse mapToWeightHistoryResponse(Revision<Integer, HealthSurvey> revision) {
        return new WeightHistoryResponse(revision.getEntity().getWeight(),
                                         revision.getRevisionInstant().orElse(Instant.MIN));
    }
}
