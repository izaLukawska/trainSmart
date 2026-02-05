package org.lukawska.trainsmart.fileexport.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EmailExcelExportCommand(@NotNull @Positive Long planId,
                                      @NotNull @Positive Long userId,
                                      @NotNull @Email String email) {}
