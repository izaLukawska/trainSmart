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
import org.lukawska.trainsmart.trainingplan.application.dto.response.UserExerciseResponse;
import org.lukawska.trainsmart.trainingplan.application.exception.UserExerciseAlreadyExistsException;
import org.lukawska.trainsmart.trainingplan.application.mapper.UserExerciseMapper;
import org.lukawska.trainsmart.trainingplan.domain.entities.UserExercise;
import org.lukawska.trainsmart.trainingplan.domain.repositories.UserExerciseRepository;
import org.mapstruct.factory.Mappers;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserExerciseService {

    private final UserExerciseRepository userExerciseRepository;

    private final UserService userService;

    private final ExerciseService exerciseService;

    private final UserExerciseMapper userExerciseMapper = Mappers.getMapper(UserExerciseMapper.class);

    @Transactional
    public List<String> syncUserExercise(@NotNull Long userId) {
        User user = userService.getUserById(userId);
        log.info("Searching for all exercises for user: {}", userId);
        List<UserExercise> currentUserExercises = userExerciseRepository.findAllByUserId(userId);
        List<Exercise> exercises = getNewExercises(currentUserExercises);
        if (exercises.isEmpty()) {
            return List.of();
        }

        List<UserExercise> newUserExercises = initializeUserExercises(exercises, user);
        log.info("Saving {} new exercises for user {}", newUserExercises.size(), userId);

        try {
            List<UserExercise> savedUserExercises = userExerciseRepository.saveAll(newUserExercises);
            return savedUserExercises.stream()
                                     .map(userExercise -> userExercise.getExercise().getName()).
                                     toList();
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

    public List<UserExerciseResponse> getUserExercisesResponse(Long userId, Boolean enabled) {
        return userExerciseMapper.toResponseList(getUserExercises(userId, enabled));
    }

    public List<UserExercise> getUserExercises(@NotNull Long userId, Boolean enabled) {
        return enabled == null ? userExerciseRepository.findAllByUserId(userId) :
                userExerciseRepository.findAllByUserIdAndEnabled(userId, enabled);
    }

    public Map<MuscleGroup, List<UserExercise>> getEnabledUserExercisesByMuscleGroup(@NotNull Long userId) {
        Map<MuscleGroup, List<UserExercise>> exercisesByMuscleGroup =
                getUserExercises(userId, true).stream()
                                              .collect(Collectors.groupingBy(
                                                      userExercise -> userExercise.getExercise().getMuscleGroup()));

        log.info("Found exercises for: {} muscle groups for user with ID: {}", exercisesByMuscleGroup.size(), userId);
        return exercisesByMuscleGroup;
    }

    public List<String> getAllExerciseNames(@NotNull Long userId) {
        return userExerciseRepository.findAllExerciseNamesByUserId(userId);
    }

    private List<UserExercise> initializeUserExercises(List<Exercise> exercises, User user) {
        return exercises.stream()
                        .map(exercise -> new UserExercise(user, exercise))
                        .toList();
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
