package org.lukawska.trainsmart.healthsurvey.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyCreateRequest;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyResponse;
import org.lukawska.trainsmart.healthsurvey.application.dto.WeightHistoryResponse;
import org.lukawska.trainsmart.healthsurvey.domain.entites.HealthSurvey;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.springframework.data.history.Revision;

import java.time.Instant;

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

    public static WeightHistoryResponse mapToWeightHistoryResponse(Revision<Integer, HealthSurvey> revision) {
        return new WeightHistoryResponse(revision.getEntity().getWeight(),
                                         revision.getRevisionInstant().orElse(Instant.MIN));
    }
}
