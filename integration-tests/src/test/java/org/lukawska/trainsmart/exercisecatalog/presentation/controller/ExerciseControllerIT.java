package org.lukawska.trainsmart.exercisecatalog.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.exercisecatalog.application.dto.ExerciseRequest;
import org.lukawska.trainsmart.exercisecatalog.application.dto.ExerciseResponse;
import org.lukawska.trainsmart.exercisecatalog.application.exception.ExceptionType;
import org.lukawska.trainsmart.exercisecatalog.application.exception.ExerciseException;
import org.lukawska.trainsmart.exercisecatalog.application.service.ExerciseService;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.ExerciseType;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExerciseController.class)
@ActiveProfiles("test")
class ExerciseControllerIT {

    @MockitoBean
    private ExerciseService exerciseService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateExercise() throws Exception {
        //given
        final ExerciseRequest exerciseRequest = new ExerciseRequest("pull up", MuscleGroup.BACK, ExerciseType.OTHER);

        //when && then
        RequestBuilder request = post("/exercises").contentType(MediaType.APPLICATION_JSON)
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
        mockMvc.perform(get("/exercises/name").param("name", name).contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value(name));
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
        RequestBuilder request = get("/exercises/muscle-group").param("muscleGroup", muscleGroup.name())
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
        mockMvc.perform(post("/exercises").contentType(MediaType.APPLICATION_JSON).content("{}"))
               .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenNotFound() throws Exception {
        //given
        final String name = "invalid";
        when(exerciseService.getExerciseByName(name)).thenThrow(
                new ExerciseException(ExceptionType.EXERCISE_NOT_FOUND));

        //when && then
        mockMvc.perform(get("/exercises/name").contentType(MediaType.APPLICATION_JSON).param("name", name))
               .andExpect(status().isNotFound());
    }
}
