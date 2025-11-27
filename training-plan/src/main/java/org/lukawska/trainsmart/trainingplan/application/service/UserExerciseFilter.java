package org.lukawska.trainsmart.trainingplan.application.service;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.healthsurvey.application.service.HealthSurveyService;
import org.lukawska.trainsmart.openai.infra.adapter.OpenAiAdapter;
import org.lukawska.trainsmart.openai.infra.dto.ChatRolesRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserExerciseFilter {

    private final HealthSurveyService healthSurveyService;

    private final UserExerciseService userExerciseService;

    private final OpenAiAdapter openAiAdapter;

    public void updateUnsafeExercises(Long userId) {
        Set<String> injuries = healthSurveyService.getAllInjuriesByUserId(userId);
        List<String> exercisesName = userExerciseService.getAllExerciseNames(userId);

        ChatRolesRequest request = prepareRequest(injuries, exercisesName);
        String openAiResponse = openAiAdapter.sendPrompt(request);
        List<String> disabledExercises = getDisabledExerciseList(openAiResponse);

        userExerciseService.updateUserExerciseEnabledStatus(userId, disabledExercises);
    }

    private ChatRolesRequest prepareRequest(Set<String> injuries, List<String> exerciseNames) {
        String injuriesStr = String.join(", ", injuries);
        String exercisesStr = String.join(", ", exerciseNames);
        String systemPrompt = "You are an expert in safe workout and exercise selection.";
        String userPrompt = String.format(
                "Given the following injuries: %s, return only the exercises from this list that CANNOT be safely " +
                        "performed: %s. Return the names in lower case and as a comma-separated list.",
                injuriesStr, exercisesStr
        );

        return new ChatRolesRequest(systemPrompt, userPrompt);
    }

    private List<String> getDisabledExerciseList(String disabledExercisesStr) {
        return Arrays.stream(disabledExercisesStr.split(","))
                     .map(String::trim)
                     .filter(s -> !s.isEmpty())
                     .toList();
    }
}
