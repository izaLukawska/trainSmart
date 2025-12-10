package org.lukawska.trainsmart.exercisecatalog.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.commons.jwt.JwtService;
import org.lukawska.trainsmart.exercisecatalog.application.dto.ExerciseRequest;
import org.lukawska.trainsmart.exercisecatalog.application.dto.ExerciseResponse;
import org.lukawska.trainsmart.exercisecatalog.application.exception.ExceptionType;
import org.lukawska.trainsmart.exercisecatalog.application.exception.ExerciseException;
import org.lukawska.trainsmart.exercisecatalog.application.service.ExerciseService;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObjects.ExerciseType;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObjects.MuscleGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExerciseController.class)
@ActiveProfiles("test")
@WithMockUser
class ExerciseControllerIT {

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private ExerciseService exerciseService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateExercise() throws Exception {
        //given
        final ExerciseRequest exerciseRequest = new ExerciseRequest("pull up", MuscleGroup.BACK, ExerciseType.OTHER);
        final ObjectMapper objectMapper = new ObjectMapper();

        //when && then
        RequestBuilder request = post("/exercises").contentType(MediaType.APPLICATION_JSON)
                                                   .with(csrf())
                                                   .content(objectMapper.writeValueAsString(exerciseRequest));
        mockMvc.perform(request)
               .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnExerciseByName() throws Exception {
        //given
        final String name = "crunches";
        final ExerciseResponse exerciseResponse = new ExerciseResponse(1L, name);
        when(exerciseService.getExerciseByName(name)).thenReturn(exerciseResponse);

        //when && then
        RequestBuilder request = get("/exercises/name").param("name", name)
                                                       .contentType(MediaType.APPLICATION_JSON)
                                                       .with(csrf());
        mockMvc.perform(request).andExpect(status().isOk()).andExpect(jsonPath("$.name").value(name));
    }

    @Test
    void shouldReturnExercisesByMuscleGroup() throws Exception {
        //given
        final MuscleGroup muscleGroup = MuscleGroup.QUADS;
        final ExerciseResponse exerciseResponse1 = new ExerciseResponse(1L, "front squat");
        final ExerciseResponse exerciseResponse2 = new ExerciseResponse(2L, "goblet squat");
        final List<ExerciseResponse> expectedResponse = List.of(exerciseResponse1, exerciseResponse2);
        when(exerciseService.getExercisesByMuscleGroup(muscleGroup)).thenReturn(expectedResponse);

        //when && then
        RequestBuilder request = get("/exercises/muscle-group").with(csrf())
                                                               .param("muscleGroup", muscleGroup.name())
                                                               .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request)
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(2))
               .andExpect(jsonPath("$[0].name").value(exerciseResponse1.name()))
               .andExpect(jsonPath("$[1].name").value(exerciseResponse2.name()));
    }

    @Test
    void shouldReturnBadRequestWhenInvalidRequestBody() throws Exception {
        //when && then
        mockMvc.perform(post("/exercises").with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{}"))
               .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenNotFound() throws Exception {
        //given
        final String name = "invalid";
        when(exerciseService.getExerciseByName(name)).thenThrow(
                new ExerciseException(ExceptionType.EXERCISE_NOT_FOUND));

        //when && then
        RequestBuilder request = get("/exercises/name").with(csrf())
                                                       .contentType(MediaType.APPLICATION_JSON)
                                                       .param("name", name);
        mockMvc.perform(request)
               .andExpect(status().isNotFound());
    }
}
