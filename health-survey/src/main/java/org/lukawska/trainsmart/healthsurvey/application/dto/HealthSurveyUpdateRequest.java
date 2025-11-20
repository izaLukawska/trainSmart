package org.lukawska.trainsmart.healthsurvey.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class HealthSurveyUpdateRequest {

    @Positive
    private Integer weight;

    @Size(max = 5)
    private Set<@NotBlank String> injuries;

    public HealthSurveyUpdateRequest(Set<String> injuries) {
        this.injuries = injuries;
    }

    public HealthSurveyUpdateRequest(Integer weight) {
        this.weight = weight;
    }
}
