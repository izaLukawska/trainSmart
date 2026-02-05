package org.lukawska.trainsmart.healthsurvey.application.dto;

import java.time.Instant;

public record WeightHistoryResponse(Integer weight, Instant updateDate) {}
