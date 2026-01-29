package org.lukawska.trainsmart.gymfinder.application.service;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.gymfinder.application.dto.GymContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GymLocationService {

    private final RestClient restClient;

    public List<GymContext> getTop5Gyms(double userLat, double userLon) {
        try {
            String query = String.format(Locale.US,
                                         "[out:json][timeout:10];node[\"leisure\"=\"fitness_centre\"](around:50000," +
                                                 "%f," +
                                                 "%f);out body;",
                                         userLat, userLon);

            Map<String, Object> rawResponse = restClient.get()
                                                        .uri(uriBuilder -> uriBuilder.path("/interpreter")
                                                                                     .queryParam("data", query)
                                                                                     .build())
                                                        .retrieve()
                                                        .body(new ParameterizedTypeReference<>() {});

            if (rawResponse == null || !rawResponse.containsKey("elements")) {
                return Collections.emptyList();
            }

            // Extract the list from the "elements" key
            List<Map<String, Object>> elements = (List<Map<String, Object>>) rawResponse.get("elements");

            return elements.stream()
                           .map(element -> {
                               Map<String, String> tags = (Map<String, String>) element.get("tags");
                               String name = (tags != null) ? tags.getOrDefault("name", "Unnamed Gym") : "Unnamed Gym";
                               double lat = ((Number) element.get("lat")).doubleValue();
                               double lon = ((Number) element.get("lon")).doubleValue();

                               return new GymContext(name, lat, lon, calculateDistance(userLat, userLon, lat, lon));
                           })
                           .sorted(Comparator.comparingDouble(GymContext::distance))
                           .limit(5)
                           .toList();

        } catch (Exception e) {
            System.err.println("Error fetching gyms: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371; // Kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }
}
