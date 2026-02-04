package org.lukawska.trainsmart.trainingplan.application.preparation.processors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.openai.infra.adapter.OpenAiAdapter;
import org.lukawska.trainsmart.trainingplan.application.service.UserExerciseService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserExerciseFilterTest {

    @Mock
    private UserExerciseService userExerciseService;

    @Mock
    private OpenAiAdapter openAiAdapter;

    @InjectMocks
    private UserExerciseFilter userExerciseFilter;

    @Test
    void shouldDelegateUpdateStatusToOpenAiAdapter() {
        //given
        final Long userId = 1L;
        final Set<String> injuries = Set.of("knee");
        final List<String> exerciseNames = List.of("back squat", "pull up");
        when(openAiAdapter.sendPrompt(any())).thenReturn("back squat");

        //when
        userExerciseFilter.updateUnsafeExercises(userId, injuries, exerciseNames);

        //then
        verify(userExerciseService).updateUserExerciseEnabledStatus(userId, List.of("back squat"));
    }
}
