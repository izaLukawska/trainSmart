package org.lukawska.trainsmart.healthsurvey.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateInjuriesRequest(@NotNull
                                    Long userId,
                                    @NotNull
                                    @Size(max = 10)
                                    @Valid
                                    List<@NotBlank String> newInjuries) {
}
