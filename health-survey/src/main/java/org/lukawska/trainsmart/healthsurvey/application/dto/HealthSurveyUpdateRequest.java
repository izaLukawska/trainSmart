package org.lukawska.trainsmart.healthsurvey.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.Set;

@Builder
public record HealthSurveyUpdateRequest(@Positive Integer weight, Set<@NotBlank String> injuries) {}
