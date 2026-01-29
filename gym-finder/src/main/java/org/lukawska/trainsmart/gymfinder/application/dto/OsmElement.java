package org.lukawska.trainsmart.gymfinder.application.dto;

import java.util.Map;

public record OsmElement(double lat, double lon, Map<String, String> tags) {
}
