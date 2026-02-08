package org.lukawska.trainsmart.trainingplan.presentation.controller;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.commons.jwt.JwtService;
import org.lukawska.trainsmart.security.auth.UserDetailsServiceImpl;
import org.lukawska.trainsmart.trainingplan.application.service.TrainingPlanService;
import org.lukawska.trainsmart.trainingplan.model.SliceTrainingPlanSummaryResponse;
import org.lukawska.trainsmart.trainingplan.model.TrainingPlanResponse;
import org.lukawska.trainsmart.trainingplan.model.TrainingTypeEnum;
import org.lukawska.trainsmart.trainingplan.presentation.controllers.TrainingPlanController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainingPlanController.class)
@WithMockUser
public class TrainingPlanControllerIT {

    private final Long USER_ID = 1L;
    private final Long PLAN_ID = 50L;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TrainingPlanService trainingPlanService;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void shouldGetTrainingPlanByPlanIdAndUserId() throws Exception {
        //given
        final TrainingTypeEnum trainingTypeEnum = TrainingTypeEnum.STRENGTH;
        final TrainingPlanResponse response = TrainingPlanResponse.builder()
                                                                  .planId(PLAN_ID)
                                                                  .trainingType(trainingTypeEnum)
                                                                  .build();

        when(trainingPlanService.getTrainingPlanResponseByIdAndUserId(PLAN_ID, USER_ID))
                .thenReturn(response);

        //when && then
        mockMvc.perform(get("/users/user/{userId}/training-plans/{planId}", USER_ID, PLAN_ID).with(csrf()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.planId").value(PLAN_ID))
               .andExpect(jsonPath("$.trainingType").value(trainingTypeEnum.getValue()));
    }

    @Test
    void shouldReturn204WhenDeleteTrainingPlan() throws Exception {
        //given
        doNothing().when(trainingPlanService).deleteTrainingPlanByIdAndUserId(PLAN_ID, USER_ID);

        //when && then
        mockMvc.perform(delete("/users/user/{userId}/training-plans/{planId}", USER_ID, PLAN_ID).with(csrf()))
               .andExpect(status().isNoContent());
    }

    @Test
    void shouldGetAllTrainingPlansSlice() throws Exception {
        //given
        final SliceTrainingPlanSummaryResponse slice = SliceTrainingPlanSummaryResponse.builder()
                                                                                       .content(List.of())
                                                                                       .pageNumber(0)
                                                                                       .hasNext(false)
                                                                                       .build();
        when(trainingPlanService.getAllTrainingPlansSummaryByUserId(eq(USER_ID), any(), any())).thenReturn(slice);

        //when && then
        mockMvc.perform(get("/users/user/{userId}/training-plans/all", USER_ID)
                                .with(csrf())
                                .param("pageNumber", "0")
                                .param("pageSize", "10")
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.hasNext").value(false));
    }
}
