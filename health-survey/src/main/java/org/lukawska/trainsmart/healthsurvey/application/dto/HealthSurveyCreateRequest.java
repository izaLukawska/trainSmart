package org.lukawska.trainsmart.healthsurvey.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.lukawska.trainsmart.healthsurvey.domain.valueObjects.Gender;

import java.util.Set;

public record HealthSurveyCreateRequest(@NotNull Gender gender,
                                        @Positive @NotNull Integer height,
                                        @Positive @NotNull Integer weight,
                                        @NotNull Set<@NotBlank String> injuries) {}
