package org.lukawska.trainsmart.healthsurvey.application.dto;

import org.lukawska.trainsmart.healthsurvey.domain.valueObjects.Gender;

public record HealthSurveyResponse(Long id, Gender gender, Integer height, Integer weight, int injuriesCount) {}
