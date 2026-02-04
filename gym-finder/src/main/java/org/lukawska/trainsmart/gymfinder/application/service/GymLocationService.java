package org.lukawska.trainsmart.gymfinder.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.commons.redis.RedisConfig;
import org.lukawska.trainsmart.gymfinder.application.dto.GymSearchResult;
import org.lukawska.trainsmart.gymfinder.application.exception.GymSearchException;
import org.lukawska.trainsmart.gymfinder.application.mapper.FindGymMapper;
import org.lukawska.trainsmart.gymfinder.domain.util.GeoDistanceCalculator;
import org.lukawska.trainsmart.gymfinder.domain.valueObject.GeoPoint;
import org.lukawska.trainsmart.gymfinder.model.FindGymRequest;
import org.lukawska.trainsmart.gymfinder.model.FindGymResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class GymLocationService {

    private final RestClient restClient;

    private final static Integer GYM_COUNT_LIMIT = 5;

    private static final String SEARCH_QUERY = """
            [out:json][timeout:10];
            node["leisure"="fitness_centre"]
              (around:%d,%f,%f);
            out body;
            """;

    @Retryable(retryFor = {ResourceAccessException.class}, backoff = @Backoff(delay = 5000))
    @Cacheable(value = RedisConfig.NEARBY_GYMS_CACHE,
               key = "{#findGymRequest.userLatitude, #findGymRequest.userLongitude}",
               unless = "#result.isEmpty()")
    public List<FindGymResponse> getGymsNearby(FindGymRequest findGymRequest) {
        GymSearchResult response = searchForGyms(findGymRequest);

        if (CollectionUtils.isEmpty(response.elements())) {
            log.info("No gyms found nearby.");
            return List.of();
        }

        log.info("Found {} gyms nearby.", response.elements().size());
        GeoPoint userGeo = new GeoPoint(findGymRequest.getUserLatitude(), findGymRequest.getUserLongitude());

        return response.elements()
                       .stream()
                       .map(location -> {
                           GeoPoint gymLocation = new GeoPoint(location.lat(), location.lon());
                           double distanceKm = GeoDistanceCalculator.haversineDistanceKm(userGeo, gymLocation);
                           return FindGymMapper.mapToFindGymResponse(location, distanceKm);
                       })
                       .sorted(Comparator.comparingDouble(FindGymResponse::getDistance))
                       .limit(GYM_COUNT_LIMIT)
                       .toList();
    }

    @SuppressWarnings("unused")
    @Recover
    List<FindGymResponse> recover(GymSearchException e, FindGymRequest findGymRequest) {
        log.error("Failed to fetch gyms after retries: lat: {}, lon: {}",
                  findGymRequest.getUserLatitude(), findGymRequest.getUserLongitude(), e);
        return List.of();
    }

    private GymSearchResult searchForGyms(FindGymRequest findGymRequest) {
        log.info("Calling gym provider");
        String query = String.format(Locale.US, SEARCH_QUERY, findGymRequest.getSearchRadiusMeters(),
                                     findGymRequest.getUserLatitude(), findGymRequest.getUserLongitude());
        try {
            return restClient.get()
                             .uri(uriBuilder -> uriBuilder.path("/interpreter")
                                                          .queryParam("data", query)
                                                          .build())
                             .retrieve()
                             .onStatus(HttpStatusCode::isError, (request, response) -> {
                                 log.error("API call error: {} {}", response.getStatusCode(), response.getStatusText());
                                 throw new GymSearchException();
                             })
                             .body(GymSearchResult.class);
        } catch (RestClientException e) {
            throw new GymSearchException();
        }
    }
}
