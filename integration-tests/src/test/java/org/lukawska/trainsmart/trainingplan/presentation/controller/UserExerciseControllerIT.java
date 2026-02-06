package org.lukawska.trainsmart.trainingplan.presentation.controller;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.commons.jwt.JwtService;
import org.lukawska.trainsmart.security.auth.UserDetailsServiceImpl;
import org.lukawska.trainsmart.trainingplan.application.service.UserExerciseService;
import org.lukawska.trainsmart.trainingplan.model.SliceUserExerciseResponse;
import org.lukawska.trainsmart.trainingplan.model.UserExerciseResponse;
import org.lukawska.trainsmart.trainingplan.presentation.controllers.UserExerciseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@WebMvcTest(UserExerciseController.class)
class UserExerciseControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private UserExerciseService userExerciseService;

    @Test
    void shouldReturnAllUserExercisesByUserId() throws Exception {
        //given
        final Long userId = 1L;
        final UserExerciseResponse userExerciseResponse = userExerciseResponse();
        final SliceUserExerciseResponse slice = SliceUserExerciseResponse.builder()
                                                                         .content(List.of(userExerciseResponse))
                                                                         .pageNumber(0)
                                                                         .hasNext(false)
                                                                         .build();
        when(userExerciseService.getAllUserExercisesByUserId(eq(userId), any(), any())).thenReturn(slice);

        //when && then
        mockMvc.perform(get("/users/{userId}/exercises/all", userId).with(csrf())
                                                                    .param("page", "0")
                                                                    .param("size", "10")
                                                                    .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content[0].exerciseName").value(userExerciseResponse.getExerciseName()))
               .andExpect(jsonPath("$.pageNumber").value(0));
    }

    @Test
    void shouldReturnUserExerciseByUserIdAndExerciseName() throws Exception {
        //given
        final Long userId = 1L;
        final UserExerciseResponse userExerciseResponse = userExerciseResponse();
        final String exerciseName = userExerciseResponse.getExerciseName();
        when(userExerciseService.getUserExerciseByUserIdAndExerciseName(userId, exerciseName)).thenReturn(
                userExerciseResponse);

        //when && then
        mockMvc.perform(get("/users/{userId}/exercises", userId).with(csrf()).param("name", exerciseName))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.exerciseName").value(exerciseName))
               .andExpect(jsonPath("$.enabled").value(true));
    }

    private UserExerciseResponse userExerciseResponse() {
        return UserExerciseResponse.builder()
                                   .id(100L)
                                   .exerciseName("pushUp")
                                   .enabled(true)
                                   .build();
    }
}
