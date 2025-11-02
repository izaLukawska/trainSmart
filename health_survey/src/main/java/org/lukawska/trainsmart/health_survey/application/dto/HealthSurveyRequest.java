package org.lukawska.trainsmart.health_survey.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.lukawska.trainsmart.health_survey.domain.valueObjects.Gender;

import java.time.LocalDate;
import java.util.List;

public record HealthSurveyRequest(@NotNull @Positive Long userId,
                                  @NotNull Gender gender,
                                  @NotNull LocalDate birthDate,
                                  @Positive @NotNull Integer weight,
                                  @NotNull List<String> injuries) {
}
