package org.lukawska.trainsmart.trainingplan.application.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.healthsurvey.application.service.HealthSurveyService;
import org.lukawska.trainsmart.openai.infra.adapter.OpenAiAdapter;
import org.lukawska.trainsmart.openai.infra.dto.ChatRolesRequest;
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

    private final HealthSurveyService healthSurveyService;

    private final UserExerciseService userExerciseService;

    private final OpenAiAdapter openAiAdapter;

    private static final String SYSTEM_PROMPT = "You are an expert in safe workout and exercise selection.";

    private static final String USER_PROMPT_TEMPLATE = ("""
            Given the following injuries: %s, return only the exercises from this list that CANNOT be safely
            performed: %s. Return the names in lower case and as a comma-separated list.
            """).replace("\n", " ").trim();

    private static String buildUserPrompt(Set<String> injuries, List<String> exerciseNames) {
        String injuriesStr = String.join(", ", injuries);
        String exercisesStr = String.join(", ", exerciseNames);
        return String.format(USER_PROMPT_TEMPLATE, injuriesStr, exercisesStr);
    }

    public void updateUnsafeExercises(@NotNull Long userId) {
        log.info("Updating user exercise enabled status for user: {}", userId);

        Set<String> injuries = healthSurveyService.getAllInjuriesByUserId(userId);
        List<String> exercisesName = userExerciseService.getAllExerciseNames(userId);

        ChatRolesRequest request = prepareRequest(injuries, exercisesName);
        String openAiResponse = openAiAdapter.sendPrompt(request);
        List<String> disabledExercises = getDisabledExerciseList(openAiResponse);

        log.debug("Disabling {} exercises due to {} injuries", disabledExercises.size(), injuries.size());
        userExerciseService.updateUserExerciseEnabledStatus(userId, disabledExercises);
    }

    private ChatRolesRequest prepareRequest(Set<String> injuries, List<String> exerciseNames) {
        String userPrompt = buildUserPrompt(injuries, exerciseNames);
        return new ChatRolesRequest(SYSTEM_PROMPT, userPrompt);
    }

    private List<String> getDisabledExerciseList(String disabledExercisesStr) {
        return Arrays.stream(disabledExercisesStr.split(","))
                     .map(String::trim)
                     .filter(s -> !s.isEmpty())
                     .toList();
    }
}
