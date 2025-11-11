package org.lukawska.trainsmart.healthsurvey.application.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyCreateRequest;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyResponse;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyUpdateRequest;
import org.lukawska.trainsmart.healthsurvey.application.dto.WeightHistoryResponse;
import org.lukawska.trainsmart.healthsurvey.application.exception.ExceptionType;
import org.lukawska.trainsmart.healthsurvey.application.exception.HealthSurveyException;
import org.lukawska.trainsmart.healthsurvey.domain.entites.HealthSurvey;
import org.lukawska.trainsmart.healthsurvey.domain.repositories.HealthSurveyRepository;
import org.lukawska.trainsmart.shared_persistence.application.service.UserService;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.lukawska.trainsmart.healthsurvey.application.mapper.HealthSurveyMapper.mapToEntity;
import static org.lukawska.trainsmart.healthsurvey.application.mapper.HealthSurveyMapper.mapToHealthSurveyResponse;

@Service
@RequiredArgsConstructor
public class HealthSurveyService {

    private final HealthSurveyRepository healthSurveyRepository;

    private final UserService userService;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public HealthSurveyResponse submitHealthSurvey(Long userId, HealthSurveyCreateRequest surveyRequest) {
        if (healthSurveyRepository.existsByUserId(userId)) {
            throw new HealthSurveyException(ExceptionType.HEALTH_SURVEY_ALREADY_EXISTS);
        }

        User user = userService.getUserById(userId);
        HealthSurvey healthSurvey = mapToEntity(surveyRequest, user);
        healthSurveyRepository.save(healthSurvey);

        return mapToHealthSurveyResponse(healthSurvey);
    }

    @Transactional
    public HealthSurveyResponse updateHealthSurvey(Long userId, HealthSurveyUpdateRequest surveyRequest) {
        HealthSurvey healthSurvey = getExistingHealthSurvey(userId);

        if (surveyRequest.weight() != null) {
            healthSurvey.updateWeight(surveyRequest.weight());
        }

        if (surveyRequest.injuries() != null) {
            healthSurvey.updateInjuries(surveyRequest.injuries());
        }

        healthSurveyRepository.save(healthSurvey);

        return mapToHealthSurveyResponse(healthSurvey);
    }

    @Transactional(readOnly = true)
    public HealthSurveyResponse getHealthSurveyByUserIdResponse(Long userId) {
        return mapToHealthSurveyResponse(getExistingHealthSurvey(userId));
    }

    @Transactional(readOnly = true)
    public List<String> getAllInjuriesByUserId(Long userId) {
        return getExistingHealthSurvey(userId).getInjuries();
    }

    @Transactional
    public void deleteHealthSurveyByUserId(Long userId) {
        HealthSurvey healthSurvey = getExistingHealthSurvey(userId);
        healthSurveyRepository.delete(healthSurvey);
    }

    @Transactional(readOnly = true)
    public List<WeightHistoryResponse> getWeightHistoryByUserId(Long userId) {
        HealthSurvey healthSurvey = getExistingHealthSurvey(userId);
        Long surveyId = healthSurvey.getId();

        AuditReader reader = AuditReaderFactory.get(entityManager);
        List<Number> revisions = reader.getRevisions(HealthSurvey.class, surveyId);

        return mapRevisionsToHistory(reader, revisions, surveyId);
    }

    private List<WeightHistoryResponse> mapRevisionsToHistory(AuditReader reader, List<Number> revisions,
                                                              Long surveyId) {
        List<WeightHistoryResponse> history = new ArrayList<>();

        for (Number rev : revisions) {
            HealthSurvey revEntity = reader.find(HealthSurvey.class, surveyId, rev);
            Date revDate = reader.getRevisionDate(rev);
            history.add(new WeightHistoryResponse(revEntity.getWeight(), revDate.toInstant()));
        }

        return history;
    }

    private HealthSurvey getExistingHealthSurvey(Long userId) {
        return healthSurveyRepository.findByUserId(userId)
                                     .orElseThrow(() -> new HealthSurveyException(
                                             ExceptionType.HEALTH_SURVEY_NOT_FOUND));
    }
}
