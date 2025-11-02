package org.lukawska.trainsmart.health_survey.domain.entites;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.lukawska.trainsmart.health_survey.domain.exception.InvalidInjuryException;
import org.lukawska.trainsmart.health_survey.domain.valueObjects.Gender;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "health_survey")
@Getter
public class HealthSurvey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @NotNull
    @Past
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    @Positive
    @NotNull
    private Integer weight;

    @ElementCollection
    @CollectionTable(name = "health_survey_injuries", joinColumns = @JoinColumn(name = "health_survey_id"))
    @Column(name = "injuries", nullable = false)
    private List<String> injuries;

    @Builder
    private HealthSurvey(User user, Gender gender, LocalDate birthDate, Integer weight, List<String> injuries) {
        this.user = user;
        this.gender = gender;
        this.birthDate = birthDate;
        this.weight = weight;
        this.injuries = injuries == null ? new ArrayList<>() : injuries;
    }

    public void updateWeight(Integer weight) {
        this.weight = weight;
    }

    @Transient
    public int getAge() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public void addInjury(String injury) {
        validateInjury(injury);
        if (!injuries.contains(injury)) {
            injuries.add(injury);
        }
    }

    public void addInjuries(List<String> newInjuries) {
        if (newInjuries == null) {
            return;
        }

        newInjuries.stream()
                   .distinct()
                   .forEach(this::addInjury);
    }

    public void removeInjury(String injury) {
        this.injuries.remove(injury);
    }

    public void removeAllInjuries() {
        this.injuries.clear();
    }

    private void validateInjury(String injury) {
        if (StringUtils.isBlank(injury)) {
            throw new InvalidInjuryException("Invalid injury input");
        }
    }
}
