package org.lukawska.trainsmart.healthsurvey.domain.entites;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lukawska.trainsmart.healthsurvey.domain.valueObjects.Gender;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;

import java.time.Instant;
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
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private Integer height;

    @Column(nullable = false)
    private Integer weight;

    @ElementCollection
    @CollectionTable(name = "health_survey_injuries", joinColumns = @JoinColumn(name = "health_survey_id"))
    @Column(nullable = false)
    private List<String> injuries;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "weight_updated_at")
    private Instant weightUpdatedAt;

    @Builder
    private HealthSurvey(User user, Gender gender, Integer height, Integer weight, List<String> injuries) {
        this.user = user;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.injuries = injuries == null ? new ArrayList<>() : injuries;
    }

    public void updateWeight(Integer weight) {
        this.weight = weight;
        this.weightUpdatedAt = Instant.now();
    }

    public void updateInjuries(List<String> injuries) {
        this.injuries = injuries;
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = Instant.now();
    }
}
