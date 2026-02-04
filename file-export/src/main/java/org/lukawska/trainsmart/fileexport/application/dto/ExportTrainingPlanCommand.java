package org.lukawska.trainsmart.fileexport.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.lukawska.trainsmart.fileexport.domain.export.ExportFormat;

public record ExportTrainingPlanCommand(@NotNull @Positive Long planId,
                                        @NotNull @Positive Long userId,
                                        @NotNull ExportFormat exportFormat) {
}
