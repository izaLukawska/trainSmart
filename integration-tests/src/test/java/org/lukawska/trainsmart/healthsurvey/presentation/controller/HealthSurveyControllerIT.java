package org.lukawska.trainsmart.healthsurvey.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.commons.jwt.JwtService;
import org.lukawska.trainsmart.healthsurvey.application.service.HealthSurveyService;
import org.lukawska.trainsmart.healthsurvey.model.*;
import org.lukawska.trainsmart.security.auth.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthSurveyController.class)
@ActiveProfiles("test")
@WithMockUser
public class HealthSurveyControllerIT {

    private static final String BASE_URL = "/users/{userId}/health-survey";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Long userId = 2L;

    @MockitoBean
    private HealthSurveyService healthSurveyService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnHealthSurveyResponseByUserId() throws Exception {
        //given
        final HealthSurveyResponse response = healthSurveyResponse();
        when(healthSurveyService.getHealthSurveyByUserIdResponse(userId)).thenReturn(response);

        //when && then
        mockMvc.perform(get(BASE_URL, userId).contentType(MediaType.APPLICATION_JSON).with(csrf()))
               .andExpect(jsonPath("$.id").value(response.getId()))
               .andExpect(jsonPath("$.gender").value(response.getGender().name()))
               .andExpect(jsonPath("$.height").value(response.getHeight()))
               .andExpect(jsonPath("$.weight").value(response.getWeight()))
               .andExpect(jsonPath("$.injuriesCount").value(response.getInjuriesCount()));
    }

    @Test
    void shouldReturnAllInjuriesByUserId() throws Exception {
        //given
        final Set<String> injuries = Set.of("broken finger", "scoliosis");
        when(healthSurveyService.getAllInjuriesByUserId(userId)).thenReturn(injuries);

        //when && then
        mockMvc.perform(get(BASE_URL.concat("/injuries"), userId).contentType(MediaType.APPLICATION_JSON).with(csrf()))
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$", containsInAnyOrder(injuries.toArray(String[]::new))));
    }

    @Test
    void shouldReturnWeightHistoryResponseByUserId() throws Exception {
        //given
        final Instant baseDate = Instant.now();
        final WeightHistoryResponse response1 = new WeightHistoryResponse(50, baseDate.minus(Duration.ofDays(10)));
        final WeightHistoryResponse response2 = new WeightHistoryResponse(60, baseDate.minus(Duration.ofDays(60)));
        final WeightHistoryResponse response3 = new WeightHistoryResponse(70, baseDate);
        final List<WeightHistoryResponse> expectedList = List.of(response1, response2, response3);
        when(healthSurveyService.getWeightHistoryByUserId(userId)).thenReturn(expectedList);

        //when && then
        RequestBuilder requestBuilder = get(BASE_URL.concat("/weight-history"), userId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
               .andExpect(jsonPath("$.[0].weight").value(response1.getWeight()))
               .andExpect(jsonPath("$.[1].weight").value(response2.getWeight()))
               .andExpect(jsonPath("$.[2].weight").value(response3.getWeight()))
               .andExpect(jsonPath("$.[0].updateDate").value(response1.getInstant().toString()))
               .andExpect(jsonPath("$.[1].updateDate").value(response2.getInstant().toString()))
               .andExpect(jsonPath("$.[2].updateDate").value(response3.getInstant().toString()));
    }

    @Test
    void shouldSubmitHealthSurveyWith201Status() throws Exception {
        //given
        final HealthSurveyResponse response = healthSurveyResponse();
        final HealthSurveyCreateRequest request = new HealthSurveyCreateRequest(
                response.getGender(), response.getHeight(), response.getWeight(), Set.of());

        when(healthSurveyService.submitHealthSurvey(userId, request)).thenReturn(response);

        //when && then
        RequestBuilder requestBuilder = post(BASE_URL, userId).contentType(MediaType.APPLICATION_JSON)
                                                              .with(csrf())
                                                              .content(objectMapper.writeValueAsString(request));

        mockMvc.perform(requestBuilder).andExpect(status().isCreated());
    }

    @Test
    void shouldUpdateHealthSurveyAndReturn200() throws Exception {
        //given
        final HealthSurveyUpdateRequest updateRequest = HealthSurveyUpdateRequest.builder()
                                                                                 .injuries(Set.of())
                                                                                 .weight(60)
                                                                                 .build();
        final HealthSurveyResponse response = healthSurveyResponse();
        when(healthSurveyService.updateHealthSurvey(userId, updateRequest)).thenReturn(response);

        //when && then
        RequestBuilder requestBuilder = put(BASE_URL, userId).with(csrf())
                                                             .contentType(MediaType.APPLICATION_JSON)
                                                             .content(objectMapper.writeValueAsString(updateRequest));

        mockMvc.perform(requestBuilder).andExpect(status().isOk());
    }

    @Test
    void shouldDeleteHealthSurveyByUserId() throws Exception {
        //when && then
        mockMvc.perform(delete(BASE_URL, userId).with(csrf()))
               .andExpect(status().isNoContent());
    }

    @Test
    void shouldThrowBadRequestWhenDeleteHealthSurveyByInvalidUserId() throws Exception {
        //given
        final Long userId = -10L;

        //when && then
        mockMvc.perform(delete(BASE_URL, userId).with(csrf())).andExpect(status().isBadRequest());
    }

    private HealthSurveyResponse healthSurveyResponse() {
        return new HealthSurveyResponse(40L, GenderEnum.FEMALE, 160, 50, 0);
    }
}
