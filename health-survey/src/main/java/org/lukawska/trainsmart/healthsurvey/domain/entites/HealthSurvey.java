package org.lukawska.trainsmart.healthsurvey.domain.entites;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.envers.Audited;
import org.lukawska.trainsmart.healthsurvey.domain.valueObjects.Gender;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Entity
@Table(name = "health_survey")
@Getter
@SoftDelete
public class HealthSurvey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false, updatable = false)
    private Integer height;

    @Column(nullable = false)
    @Audited
    private Integer weight;

    @ElementCollection
    @CollectionTable(name = "health_survey_injuries", joinColumns = @JoinColumn(name = "health_survey_id"))
    @Column(nullable = false)
    private Set<String> injuries;

    @Builder
    private HealthSurvey(User user, Gender gender, Integer height, Integer weight, Set<String> injuries) {
        this.user = user;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.injuries = injuries == null ? new HashSet<>() : injuries;
    }

    public void updateWeight(Integer weight) {
        this.weight = weight;
    }

    public void updateInjuries(Set<String> injuries) {
        this.injuries = injuries;
    }
}
