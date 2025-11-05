package org.lukawska.trainsmart.healthsurvey.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateWeightRequest(@NotNull
                                  @Positive
                                  Integer newWeight) {}
