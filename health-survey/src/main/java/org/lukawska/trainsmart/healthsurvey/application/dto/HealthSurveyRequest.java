package org.lukawska.trainsmart.healthsurvey.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.lukawska.trainsmart.healthsurvey.application.validation.Adult;
import org.lukawska.trainsmart.healthsurvey.domain.valueObjects.Gender;

import java.time.LocalDate;
import java.util.List;

public record HealthSurveyRequest(@NotNull
                                  @Positive
                                  Long userId,

                                  @NotNull
                                  Gender gender,

                                  @NotNull
                                  @Adult
                                  LocalDate birthDate,

                                  @Positive
                                  @NotNull
                                  Integer weight,

                                  @NotNull
                                  @Size(max = 10)
                                  @Valid
                                  List<@NotBlank String> injuries) {}
