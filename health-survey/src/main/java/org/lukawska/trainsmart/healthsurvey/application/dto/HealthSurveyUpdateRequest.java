package org.lukawska.trainsmart.healthsurvey.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record HealthSurveyUpdateRequest(@Positive Integer weight, @Size(max = 5) List<@NotBlank String> injuries) {}
