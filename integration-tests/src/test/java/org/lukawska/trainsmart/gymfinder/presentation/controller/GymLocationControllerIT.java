package org.lukawska.trainsmart.gymfinder.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.commons.jwt.JwtService;
import org.lukawska.trainsmart.gymfinder.application.service.GymLocationService;
import org.lukawska.trainsmart.gymfinder.model.FindGymRequest;
import org.lukawska.trainsmart.gymfinder.model.FindGymResponse;
import org.lukawska.trainsmart.security.auth.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GymLocationController.class)
@WithMockUser
@ActiveProfiles("test")
class GymLocationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private GymLocationService gymLocationService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnGymsNearby() throws Exception {
        //given
        final FindGymRequest request = findGymRequest();
        final List<FindGymResponse> response = List.of(new FindGymResponse("Zdrofit", 53.23, 20.20, 12.00));
        when(gymLocationService.getGymsNearby(request)).thenReturn(response);

        //when && then
        mockMvc.perform(post("/gym/nearby").with(csrf())
                                           .contentType(MediaType.APPLICATION_JSON)
                                           .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.size()").value(1))
               .andExpect(jsonPath("$[0].name").value(response.getFirst().getName()))
               .andExpect(jsonPath("$[0].distance").value(response.getFirst().getDistance()));
    }

    private FindGymRequest findGymRequest() {
        Random random = new Random();

        int radius = random.nextInt(1, 5001);
        double lat = random.nextDouble(-90.0, 90.0);
        double lon = random.nextDouble(-180.0, 180.0);

        return new FindGymRequest(radius, lat, lon);
    }
}
