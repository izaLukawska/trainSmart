package org.lukawska.trainsmart.health_survey.application.dto;

import org.lukawska.trainsmart.health_survey.domain.valueObjects.Gender;

public record HealthSurveyResponse(Long id,
                                   Long userId,
                                   Gender gender,
                                   Integer age,
                                   Integer weight) {}
