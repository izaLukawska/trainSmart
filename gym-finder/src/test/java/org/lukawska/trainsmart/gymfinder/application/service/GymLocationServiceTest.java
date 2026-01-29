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
        final FindGymRequest request = new FindGymRequest(5000, 52.2297, 21.0122);
        final String expectedResponse = """
                {
                    "elements": [
                        {"lat": 52.2300, "lon": 21.0100, "tags": {"name": "Gym A"}},
                        {"lat": 49.2300, "lon": 21.0100, "tags": {"name": "Gym B"}}
                    ]
                }
                """;

        mockServer.expect(once(), requestTo(containsString("/interpreter")))
                  .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        //when
        List<FindGymResponse> result = gymLocationService.getGymsNearby(request);

        //then
        assertThat(result.getFirst().getName()).isEqualTo("Gym A");
        assertThat(result.getLast().getName()).isEqualTo("Gym B");
        mockServer.verify();
    }

    @Test
    void shouldReturnEmptyListWhenGymsNearbyFound() {
        //given
        final FindGymRequest request = new FindGymRequest(5000, 52.2297, 21.0122);
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
        final FindGymRequest request = new FindGymRequest(5000, 52.2, 21.0);

        // when
        List<FindGymResponse> result = gymLocationService.recover(exception, request);

        // then
        assertThat(result).isEmpty();
        mockServer.verify();
    }

    @Test
    void shouldThrowGymSearchExceptionWhenGetGymsNearby() {
        //given
        final FindGymRequest request = new FindGymRequest(5000, 52.2297, 21.0122);
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
        final FindGymRequest request = new FindGymRequest(5000, 52.2297, 21.0122);
        mockServer.expect(once(), requestTo(containsString("/interpreter")))
                  .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        //when && then
        assertThatThrownBy(() -> gymLocationService.getGymsNearby(request))
                .isInstanceOf(GymSearchException.class);

        mockServer.verify();
    }
}
