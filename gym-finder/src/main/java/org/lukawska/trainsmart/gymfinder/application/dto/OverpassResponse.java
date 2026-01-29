package org.lukawska.trainsmart.gymfinder.application.dto;

import java.util.List;

public record OverpassResponse(List<LocationResponse> elements) {}
