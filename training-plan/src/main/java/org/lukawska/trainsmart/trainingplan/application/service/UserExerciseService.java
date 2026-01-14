package org.lukawska.trainsmart.trainingplan.application.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.exercisecatalog.application.service.ExerciseService;
import org.lukawska.trainsmart.exercisecatalog.domain.entity.Exercise;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;
import org.lukawska.trainsmart.sharedpersistence.application.service.UserService;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.sharedpersistence.infrastructure.audit.AuditableEntity;
import org.lukawska.trainsmart.trainingplan.application.dto.UserExerciseFilterDto;
import org.lukawska.trainsmart.trainingplan.application.exception.ExceptionType;
import org.lukawska.trainsmart.trainingplan.application.exception.UserExerciseException;
import org.lukawska.trainsmart.trainingplan.application.specification.UserExerciseSpecificationBuilder;
import org.lukawska.trainsmart.trainingplan.domain.entities.UserExercise;
import org.lukawska.trainsmart.trainingplan.domain.repositories.UserExerciseRepository;
import org.lukawska.trainsmart.trainingplan.model.PagingRequest;
import org.lukawska.trainsmart.trainingplan.model.SliceUserExerciseResponse;
import org.lukawska.trainsmart.trainingplan.model.UserExerciseFilterRequest;
import org.lukawska.trainsmart.trainingplan.model.UserExerciseResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.lukawska.trainsmart.trainingplan.application.mapper.PageableMapper.mapToPageable;
import static org.lukawska.trainsmart.trainingplan.application.mapper.UserExerciseFilterMapper.mapToUserExerciseFilterDto;
import static org.lukawska.trainsmart.trainingplan.application.mapper.UserExerciseMapper.mapToResponse;
import static org.lukawska.trainsmart.trainingplan.application.mapper.UserExerciseMapper.mapToUserExerciseResponseSlice;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserExerciseService {

    private final UserExerciseRepository userExerciseRepository;

    private final UserService userService;

    private final ExerciseService exerciseService;

    @Transactional
    public List<String> syncUserExercise(@NotNull Long userId) {
        User user = userService.getUserById(userId);
        log.info("Syncing exercises for user: {}", userId);
        List<UserExercise> currentUserExercises = userExerciseRepository.findAllByUserId(userId);
        List<Exercise> exercises = getNewExercises(currentUserExercises);
        if (exercises.isEmpty()) {
            return List.of();
        }

        List<UserExercise> newUserExercises = exercises.stream()
                                                       .map(exercise -> new UserExercise(user, exercise))
                                                       .toList();
        log.info("Saving {} new exercises", newUserExercises.size());

        try {
            List<UserExercise> savedUserExercises = userExerciseRepository.saveAll(newUserExercises);
            log.debug("Exercise saved {}", savedUserExercises.size());
            return savedUserExercises.stream()
                                     .map(userExercise -> userExercise.getExercise().getName())
                                     .toList();
        } catch (DataIntegrityViolationException e) {
            throw new UserExerciseException(ExceptionType.USER_EXERCISE_ALREADY_EXISTS);
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

    public UserExerciseResponse getUserExerciseByUserIdAndExerciseName(Long userId, String exerciseName) {
        UserExercise userExercise = userExerciseRepository.findByUserIdAndExerciseName(userId, exerciseName)
                                                          .orElseThrow(() -> new UserExerciseException(
                                                                  ExceptionType.USER_EXERCISE_NOT_FOUND));

        log.info("Found user exercise {}", userExercise.getId());
        return mapToResponse(userExercise);
    }

    public SliceUserExerciseResponse getAllUserExercisesByUserId(Long userId, PagingRequest pagingRequest,
                                                                 UserExerciseFilterRequest filterRequest) {
        Pageable pageable = mapToPageable(pagingRequest);
        UserExerciseFilterDto userExerciseFilterDto = mapToUserExerciseFilterDto(filterRequest);
        Specification<UserExercise> specification = UserExerciseSpecificationBuilder.build(userId,
                                                                                           userExerciseFilterDto);
        Slice<UserExercise> foundExercises = userExerciseRepository.findAll(specification, pageable);
        log.info("Found {} exercises matching the criteria", foundExercises.getContent().size());

        return mapToUserExerciseResponseSlice(foundExercises);
    }

    public Map<MuscleGroup, List<UserExercise>> getEnabledUserExercisesByMuscleGroup(@NotNull Long userId) {
        Map<MuscleGroup, List<UserExercise>> groups =
                userExerciseRepository.findAllByUserIdAndEnabledIsTrue(userId)
                                      .stream()
                                      .collect(Collectors.groupingBy(userExercise -> userExercise.getExercise()
                                                                                                 .getMuscleGroup()));

        log.info("Found exercises for: {} muscle groups for user with ID: {}", groups.size(), userId);
        return groups;
    }

    public List<String> getAllExerciseNames(@NotNull Long userId) {
        return userExerciseRepository.findAllExerciseNamesByUserId(userId);
    }

    private List<Exercise> getNewExercises(List<UserExercise> userExercises) {
        if (userExercises.isEmpty()) {
            log.debug("Fetching all exercises (user has no exercises)");
            return exerciseService.getAllExercises();
        }

        Instant lastUpdate = getLastUserExerciseUpdate(userExercises);
        log.debug("Fetching exercises created after: {}", lastUpdate);
        return exerciseService.getExercisesByCreatedAtSince(lastUpdate);
    }

    private Instant getLastUserExerciseUpdate(List<UserExercise> userExercises) {
        return userExercises.stream()
                            .map(AuditableEntity::getModifiedAt)
                            .max(Instant::compareTo)
                            .orElse(Instant.MIN);
    }
}
