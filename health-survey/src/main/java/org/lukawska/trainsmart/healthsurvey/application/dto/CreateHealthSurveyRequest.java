package org.lukawska.trainsmart.healthsurvey.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.lukawska.trainsmart.healthsurvey.domain.valueObjects.Gender;

import java.util.List;

public record CreateHealthSurveyRequest(@NotNull @Positive Long userId,
                                        @NotNull Gender gender,
                                        @Positive @NotNull Integer weight,
                                        @NotNull @Size(max = 5) List<@NotBlank String> injuries) {}
