package org.lukawska.trainsmart.gymfinder.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.gymfinder.application.exception.GymSearchException;
import org.lukawska.trainsmart.gymfinder.model.FindGymRequest;
import org.lukawska.trainsmart.gymfinder.model.FindGymResponse;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@ExtendWith(MockitoExtension.class)
class GymLocationServiceTest {

    private MockRestServiceServer mockServer;

    private GymLocationService gymLocationService;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        RestClient restClient = RestClient.create(restTemplate);

        gymLocationService = new GymLocationService(restClient);
    }

    @Test
    void shouldReturnFindGymResponseListWhenGymsNearbyPresent() {
        //given
        final FindGymRequest request = findGymRequest();
        final Random random = new Random();
        final String gymA = "Gym" + random.nextInt();
        final String gymB = "Gym" + random.nextInt();
        final String expectedResponse = """
                {
                  "elements": [
                    {"lat": 52.2300, "lon": 21.0100, "tags": {"name": "%s"}},
                    {"lat": 49.2300, "lon": 21.0100, "tags": {"name": "%s"}}
                  ]
                }
                """.formatted(gymA, gymB);

        mockServer.expect(once(), requestTo(containsString("/interpreter")))
                  .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        //when
        List<FindGymResponse> result = gymLocationService.getGymsNearby(request);

        //then
        assertThat(result).extracting(FindGymResponse::getName).containsExactlyInAnyOrder(gymA, gymB);
        mockServer.verify();
    }

    @Test
    void shouldReturnEmptyListWhenGymsNearbyFound() {
        //given
        final FindGymRequest request = findGymRequest();
        final String expectedResponse = """
                {
                    "elements": []
                }
                """;

        mockServer.expect(once(), requestTo(containsString("/interpreter")))
                  .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        //when
        List<FindGymResponse> result = gymLocationService.getGymsNearby(request);

        //then
        assertThat(result).isEmpty();
        mockServer.verify();
    }

    @Test
    void shouldReturnEmptyListWhenRecoveringFromException() {
        // given
        final GymSearchException exception = new GymSearchException();
        final FindGymRequest request = findGymRequest();

        // when
        List<FindGymResponse> result = gymLocationService.recover(exception, request);

        // then
        assertThat(result).isEmpty();
        mockServer.verify();
    }

    @Test
    void shouldThrowGymSearchExceptionWhenGetGymsNearby() {
        //given
        final FindGymRequest request = findGymRequest();
        mockServer.expect(once(), requestTo(containsString("/interpreter")))
                  .andRespond(withException(new IOException("Connection reset")));

        //when && then
        assertThatThrownBy(() -> gymLocationService.getGymsNearby(request))
                .isInstanceOf(GymSearchException.class);
        mockServer.verify();
    }

    @Test
    void shouldCoverOnStatusBlockWhenApiReturns500() {
        //given
        final FindGymRequest request = findGymRequest();
        mockServer.expect(once(), requestTo(containsString("/interpreter")))
                  .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        //when && then
        assertThatThrownBy(() -> gymLocationService.getGymsNearby(request))
                .isInstanceOf(GymSearchException.class);

        mockServer.verify();
    }

    private FindGymRequest findGymRequest() {
        Random random = new Random();

        int radius = random.nextInt(1, 5001);
        double lat = random.nextDouble(-90.0, 90.0);
        double lon = random.nextDouble(-180.0, 180.0);

        return new FindGymRequest(radius, lat, lon);
    }
}
