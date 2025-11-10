package org.lukawska.trainsmart.healthsurvey.application.dto;

import org.lukawska.trainsmart.healthsurvey.domain.valueObjects.Gender;

public record HealthSurveyResponse(Long id,
                                   Long userId,
                                   Gender gender,
                                   Integer weight,
                                   int injuriesCount) {}
