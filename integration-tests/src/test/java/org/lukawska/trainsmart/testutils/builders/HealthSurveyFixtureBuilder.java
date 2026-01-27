package org.lukawska.trainsmart.testutils.builders;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.healthsurvey.domain.entites.HealthSurvey;
import org.lukawska.trainsmart.healthsurvey.domain.repositories.HealthSurveyRepository;
import org.lukawska.trainsmart.healthsurvey.domain.valueObjects.Gender;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class HealthSurveyFixtureBuilder {

    private final HealthSurveyRepository healthSurveyRepository;

    private final TestFixtures testFixtures;

    private User user;

    private Gender gender = Gender.FEMALE;

    private Integer height = 180;

    private Integer weight = 80;

    private Set<String> injuries = new HashSet<>();

    public HealthSurveyFixtureBuilder forUser(User user) {
        this.user = user;
        return this;
    }

    public HealthSurvey build() {
        User user = Optional.ofNullable(this.user).orElseGet(() -> testFixtures.user().save());
        return HealthSurvey.builder()
                           .user(user)
                           .gender(Gender.MALE)
                           .height(180)
                           .weight(80)
                           .injuries(injuries)
                           .build();
    }

    public HealthSurvey save() {
        return healthSurveyRepository.save(build());
    }
}
