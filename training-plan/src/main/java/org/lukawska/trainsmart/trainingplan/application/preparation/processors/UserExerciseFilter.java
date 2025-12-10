package org.lukawska.trainsmart.trainingplan.application.preparation.processors;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.openai.infra.adapter.OpenAiAdapter;
import org.lukawska.trainsmart.openai.infra.dto.ChatRolesRequest;
import org.lukawska.trainsmart.trainingplan.application.service.UserExerciseService;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserExerciseFilter {

    private final UserExerciseService userExerciseService;

    private final OpenAiAdapter openAiAdapter;

    private static final String SYSTEM_PROMPT = "You are an expert in safe workout and exercise selection.";

    private static final String USER_PROMPT_TEMPLATE = ("""
            Given the following injuries: %s, return only the exercises from this list that CANNOT be safely
            performed: %s. Return the names in lower case and as a comma-separated list.
            """).replace("\n", " ").trim();

    public void updateUnsafeExercises(@NotNull Long userId,
                                      @NotNull @NotEmpty Set<String> injuries,
                                      @NotNull @NotEmpty List<String> exerciseNames) {
        log.info("Updating user exercise enabled status for user: {}", userId);

        ChatRolesRequest request = prepareRequest(injuries, exerciseNames);
        String openAiResponse = openAiAdapter.sendPrompt(request);
        List<String> disabledExercises = getDisabledExerciseList(openAiResponse);

        log.debug("Disabling {} exercises due to {} injuries", disabledExercises.size(), injuries.size());
        userExerciseService.updateUserExerciseEnabledStatus(userId, disabledExercises);
    }

    private ChatRolesRequest prepareRequest(Set<String> injuries, List<String> exerciseNames) {
        String userPrompt = buildUserPrompt(injuries, exerciseNames);
        return new ChatRolesRequest(SYSTEM_PROMPT, userPrompt);
    }

    private String buildUserPrompt(Set<String> injuries, List<String> exerciseNames) {
        String injuriesStr = String.join(", ", injuries);
        String exercisesStr = String.join(", ", exerciseNames);
        return String.format(USER_PROMPT_TEMPLATE, injuriesStr, exercisesStr);
    }

    private List<String> getDisabledExerciseList(String disabledExercisesStr) {
        return Arrays.stream(disabledExercisesStr.split(","))
                     .map(String::trim)
                     .filter(s -> !s.isEmpty())
                     .toList();
    }
}
