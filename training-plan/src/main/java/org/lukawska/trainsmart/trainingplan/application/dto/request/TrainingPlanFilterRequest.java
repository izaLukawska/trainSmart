package org.lukawska.trainsmart.trainingplan.application.dto.request;

import jakarta.validation.constraints.Positive;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;
import org.springframework.data.domain.Sort;

public record TrainingPlanFilterRequest(@Positive Integer pageNumber,
                                        @Positive Integer pageSize,
                                        String sortBy,
                                        Sort.Direction sortDirection,
                                        TrainingType trainingType,
                                        PlanDuration planDuration) {}
