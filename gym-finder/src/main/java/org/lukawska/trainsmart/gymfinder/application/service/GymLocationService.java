package org.lukawska.trainsmart.gymfinder.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.gymfinder.application.dto.FindGymRequest;
import org.lukawska.trainsmart.gymfinder.application.dto.GymContextResponse;
import org.lukawska.trainsmart.gymfinder.application.dto.OverpassResponse;
import org.lukawska.trainsmart.gymfinder.domain.util.GeoDistanceCalculator;
import org.lukawska.trainsmart.gymfinder.domain.valueObject.GeoPoint;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static org.lukawska.trainsmart.gymfinder.application.mapper.GymContextMapper.mapToGymContextResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class GymLocationService {

    private final RestClient restClient;

    private final static Integer GYM_COUNT_LIMIT = 5;

    private static final String OVERPASS_QUERY = """
            [out:json][timeout:10];
            node["leisure"="fitness_centre"]
              (around:%d,%f,%f);
            out body;
            """;

    @Retryable(retryFor = {ResourceAccessException.class}, backoff = @Backoff(delay = 5000))
    @Cacheable(value = "nearbyGyms",
               key = "{#findGymRequest.userLatitude(), #findGymRequest.userLongitude()}",
               unless = "#result.isEmpty()")
    public List<GymContextResponse> getTop5Gyms(FindGymRequest findGymRequest) {
        double userLatitude = findGymRequest.userLatitude();
        double userLongitude = findGymRequest.userLongitude();
        int radiusMeters = findGymRequest.searchRadiusMeters();

        String query = String.format(Locale.US, OVERPASS_QUERY, radiusMeters, userLatitude, userLongitude);
        log.info("Looking for gyms around lat={}, lon={} with radius={}m", userLatitude, userLongitude, radiusMeters);
        GeoPoint userGeo = new GeoPoint(userLatitude, userLongitude);

        OverpassResponse response = searchForGyms(query);

        if (CollectionUtils.isEmpty(response.elements())) {
            log.info("No gyms found nearby.");
            return List.of();
        }

        return response.elements()
                       .stream()
                       .map(location -> {
                           GeoPoint gymLocation = new GeoPoint(location.lat(), location.lon());
                           double distanceKm = GeoDistanceCalculator.haversineDistanceKm(userGeo, gymLocation);
                           return mapToGymContextResponse(location, distanceKm);
                       })
                       .sorted(Comparator.comparingDouble(GymContextResponse::distance))
                       .limit(GYM_COUNT_LIMIT)
                       .toList();
    }

    @Recover
    public List<GymContextResponse> recover(ResourceAccessException e, FindGymRequest findGymRequest) {
        log.error("Failed to fetch gyms after retries: lat={}, lon={}",
                  findGymRequest.userLatitude(), findGymRequest.userLongitude(), e);
        return List.of();
    }

    private OverpassResponse searchForGyms(String query) {
        return restClient.get()
                         .uri(uriBuilder -> uriBuilder.path("/interpreter")
                                                      .queryParam("data", query)
                                                      .build())
                         .retrieve()
                         .body(OverpassResponse.class);
    }
}
