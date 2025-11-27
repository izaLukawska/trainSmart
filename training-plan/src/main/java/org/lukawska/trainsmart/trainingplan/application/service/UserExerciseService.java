package org.lukawska.trainsmart.trainingplan.application.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.exercisecatalog.application.service.ExerciseService;
import org.lukawska.trainsmart.exercisecatalog.domain.entity.Exercise;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;
import org.lukawska.trainsmart.sharedpersistence.application.service.UserService;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.sharedpersistence.infrastructure.base.BaseEntity;
import org.lukawska.trainsmart.trainingplan.application.dto.UserExerciseResponse;
import org.lukawska.trainsmart.trainingplan.application.exception.UserExerciseAlreadyExistsException;
import org.lukawska.trainsmart.trainingplan.application.mapper.UserExerciseMapper;
import org.lukawska.trainsmart.trainingplan.domain.entity.UserExercise;
import org.lukawska.trainsmart.trainingplan.domain.repository.UserExerciseRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.lukawska.trainsmart.trainingplan.application.mapper.UserExerciseMapper.mapToUserExercises;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserExerciseService {

    private final UserExerciseRepository userExerciseRepository;

    private final UserService userService;

    private final ExerciseService exerciseService;

    @Transactional
    public void syncUserExercise(@NotNull Long userId) {
        User user = userService.getUserById(userId);
        log.info("Searching for all exercises for user: {}", userId);
        List<UserExercise> currentUserExercises = userExerciseRepository.findAllByUserId(userId);
        List<Exercise> exercises = getNewExercises(currentUserExercises);
        if (exercises.isEmpty()) {
            return;
        }

        List<UserExercise> newUserExercises = mapToUserExercises(exercises, user);

        try {
            log.info("Saving {} exercises for user {}", newUserExercises.size(), userId);
            userExerciseRepository.saveAll(newUserExercises);
        } catch (DataIntegrityViolationException e) {
            throw new UserExerciseAlreadyExistsException(userId);
        }
    }

    @Transactional
    public void updateUserExerciseEnabledStatus(@NotNull Long userId, @NotNull List<String> disabledExerciseNames) {
        User user = userService.getUserById(userId);
        if (disabledExerciseNames.isEmpty()) {
            log.info("Disabled exercises list is empty. Enabling all exercises for user: {}", userId);
            userExerciseRepository.enableAllByUserId(userId, Instant.now());
        } else {
            log.info("Disabling exercises: {} for user: {}", disabledExerciseNames.size(), userId);
            userExerciseRepository.updateEnabledByUserIdAndNames(user.getId(), disabledExerciseNames, Instant.now());
        }
    }

    public List<UserExerciseResponse> getUserExercisesResponse(@NotNull Long userId, Boolean enabled) {
        return getUserExercises(userId, enabled).stream()
                                                .map(UserExerciseMapper::mapToResponse)
                                                .toList();
    }

    public List<UserExercise> getUserExercises(@NotNull Long userId, Boolean enabled) {
        return enabled == null ? userExerciseRepository.findAllByUserId(userId) :
                userExerciseRepository.findAllByUserIdAndEnabled(userId, enabled);
    }

    public Map<MuscleGroup, List<UserExercise>> getEnabledUserExercisesByMuscleGroup(@NotNull Long userId) {
        return getUserExercises(userId, true).stream()
                                             .collect(Collectors.groupingBy(
                                                     userExercise -> userExercise.getExercise().getMuscleGroup()));
    }

    public List<String> getAllExerciseNames(@NotNull Long userId) {
        return userExerciseRepository.findAllExerciseNamesByUserId(userId);
    }

    private Instant getLastUserExerciseUpdate(List<UserExercise> userExercises) {
        return userExercises.stream()
                            .map(BaseEntity::getModifiedAt)
                            .max(Instant::compareTo)
                            .orElse(Instant.MIN);
    }

    private List<Exercise> getNewExercises(List<UserExercise> userExercises) {
        if (userExercises.isEmpty()) {
            log.debug("Fetching all exercises (user has no exercises)");
            return exerciseService.getAllExercises();
        } else {
            Instant lastUpdate = getLastUserExerciseUpdate(userExercises);
            log.debug("Fetching exercises created after: {}", lastUpdate);
            return exerciseService.getExercisesFrom(lastUpdate);
        }
    }
}
