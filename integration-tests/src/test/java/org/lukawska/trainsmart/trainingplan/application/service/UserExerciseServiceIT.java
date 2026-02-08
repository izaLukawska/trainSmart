package org.lukawska.trainsmart.trainingplan.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.exercisecatalog.domain.entities.Exercise;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObjects.MuscleGroup;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.domain.entities.UserExercise;
import org.lukawska.trainsmart.trainingplan.domain.repositories.UserExerciseRepository;
import org.lukawska.trainsmart.trainingplan.model.PagingRequest;
import org.lukawska.trainsmart.trainingplan.model.SliceUserExerciseResponse;
import org.lukawska.trainsmart.trainingplan.model.UserExerciseFilterRequest;
import org.lukawska.trainsmart.trainingplan.model.UserExerciseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import({TestFixtures.class, PostgresTestConfig.class})
@Transactional
class UserExerciseServiceIT {

    @Autowired
    private UserExerciseRepository userExerciseRepository;

    @Autowired
    private UserExerciseService userExerciseService;

    @Autowired
    private TestFixtures testFixtures;

    private User user;

    private List<UserExercise> userExercises;

    @BeforeEach
    void setUp() {
        user = testFixtures.user().save();
        userExercises = testFixtures.setUpUserExercises(user);
    }

    @Test
    void shouldReturnNewExerciseNamesWhenSyncUserExerciseAndNewExercisesAdded() {
        //given
        final Exercise exercise = testFixtures.exercise().save();
        final Long userId = user.getId();

        //when
        List<String> result = userExerciseService.syncUserExercise(userId);

        //then
        assertThat(result.getFirst()).isEqualTo(exercise.getName());
    }

    @Test
    void shouldReturnEmptyListWhenSyncUserExercisesAndNoNewExercises() {
        //given
        final Long userId = user.getId();

        //when
        List<String> result = userExerciseService.syncUserExercise(userId);

        //then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldUpdateUserExerciseEnabledStatusAndDisabledExerciseNamesProvided() {
        //given
        final Long userId = user.getId();
        final List<String> disabledNames = List.of(userExercises.getLast().getExercise().getName());

        //when
        userExerciseService.updateUserExerciseEnabledStatus(userId, disabledNames);

        //then
        List<UserExercise> updated = userExerciseRepository.findAllByUserId(userId);
        UserExercise userExercise1 = updated.getFirst();
        UserExercise userExercise2 = updated.get(1);
        UserExercise userExercise3 = updated.getLast();

        assertThat(userExercise1.isEnabled()).isTrue();
        assertThat(userExercise2.isEnabled()).isTrue();
        assertThat(userExercise3.isEnabled()).isFalse();
    }

    @Test
    void shouldEnableAllExercisesWhenUpdateUserExerciseEnabledStatusAndDisabledExerciseNamesEmpty() {
        //given
        final List<String> disabledNames = List.of();
        final Long userId = user.getId();

        //when
        userExerciseService.updateUserExerciseEnabledStatus(userId, disabledNames);

        //then
        List<UserExercise> updated = userExerciseRepository.findAllByUserId(userId);
        UserExercise userExercise1 = updated.getFirst();
        UserExercise userExercise2 = updated.get(1);
        UserExercise userExercise3 = updated.getLast();

        assertThat(userExercise1.isEnabled()).isTrue();
        assertThat(userExercise2.isEnabled()).isTrue();
        assertThat(userExercise3.isEnabled()).isTrue();
    }

    @Test
    void shouldReturnSliceUserExerciseResponseWhenGetAllUserExercisesByUserId() {
        //given
        final Long userId = user.getId();
        final PagingRequest pagingRequest = PagingRequest.builder()
                                                         .pageSize(1)
                                                         .pageNumber(1)
                                                         .direction(PagingRequest.DirectionEnum.ASC)
                                                         .sortBy(PagingRequest.SortByEnum.ID)
                                                         .build();
        final UserExerciseFilterRequest filterRequest = UserExerciseFilterRequest.builder()
                                                                                 .enabled(true)
                                                                                 .build();

        //when
        SliceUserExerciseResponse result = userExerciseService.getAllUserExercisesByUserId(
                userId, pagingRequest, filterRequest);

        //then
        assertThat(result.getContent().size()).isEqualTo(pagingRequest.getPageSize());
        assertThat(result.getPageNumber()).isEqualTo(pagingRequest.getPageNumber());
        assertThat(result.getPageSize()).isEqualTo(pagingRequest.getPageSize());
        assertThat(result.getIsFirst()).isFalse();
        assertThat(result.getHasNext()).isTrue();
    }

    @Test
    void shouldReturnUserExerciseResponseWhenGetUserExerciseByUserIdAndExerciseName() {
        //given
        final Long userId = user.getId();
        final UserExercise userExercise = userExercises.getFirst();
        final String name = userExercise.getExercise().getName();

        //when
        UserExerciseResponse result = userExerciseService.getUserExerciseByUserIdAndExerciseName(userId, name);

        //then
        assertThat(result.getExerciseName()).isEqualTo(name);
        assertThat(result.getEnabled()).isEqualTo(userExercise.isEnabled());
        assertThat(result.getId()).isEqualTo(userExercise.getId());
        assertThat(result.getLastUsedAt()).isEqualTo(userExercise.getLastUsedAt());
    }

    @Test
    void shouldReturnAllExerciseNamesByUserId() {
        //given
        final Long userId = user.getId();

        //when
        List<String> result = userExerciseService.getAllExerciseNames(userId);

        //then
        assertThat(result.getFirst()).isEqualTo(userExercises.getFirst().getExercise().getName());
        assertThat(result.get(1)).isEqualTo(userExercises.get(1).getExercise().getName());
        assertThat(result.get(2)).isEqualTo(userExercises.get(2).getExercise().getName());
    }

    @Test
    void shouldReturnUserExerciseByMuscleGroupMapWhenGetEnabledUserExercisesByMuscleGroup() {
        //given
        final Long userId = user.getId();

        //when
        Map<MuscleGroup, List<UserExercise>> result = userExerciseService.getEnabledUserExercisesByMuscleGroup(userId);

        //then
        List<UserExercise> updatedUserExercises = userExerciseRepository.findAllByUserId(userId);
        MuscleGroup muscleGroup1 = updatedUserExercises.getFirst().getExercise().getMuscleGroup();
        MuscleGroup muscleGroup2 = updatedUserExercises.getLast().getExercise().getMuscleGroup();
        MuscleGroup muscleGroup3 = updatedUserExercises.get(1).getExercise().getMuscleGroup();

        assertThat(result.keySet()).containsAll(List.of(muscleGroup1, muscleGroup2, muscleGroup3));
    }
}
