package org.lukawska.trainsmart.trainingplan.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.exercisecatalog.application.service.ExerciseService;
import org.lukawska.trainsmart.exercisecatalog.domain.entity.Exercise;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;
import org.lukawska.trainsmart.sharedpersistence.application.service.UserService;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.application.dto.request.PagingRequest;
import org.lukawska.trainsmart.trainingplan.application.dto.request.UserExerciseFilterRequest;
import org.lukawska.trainsmart.trainingplan.application.dto.response.UserExerciseResponse;
import org.lukawska.trainsmart.trainingplan.application.exception.ExceptionType;
import org.lukawska.trainsmart.trainingplan.application.exception.UserExerciseException;
import org.lukawska.trainsmart.trainingplan.domain.entities.UserExercise;
import org.lukawska.trainsmart.trainingplan.domain.repositories.UserExerciseRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserExerciseServiceTest {

    @Mock
    private UserExerciseRepository userExerciseRepository;

    @Mock
    private UserService userService;

    @Mock
    private ExerciseService exerciseService;

    @InjectMocks
    private UserExerciseService userExerciseService;

    private static final Long userId = 1L;

    @Test
    void shouldReturnNewUserExercisesNamesWhenSyncUserExercise() {
        // given
        final List<UserExercise> savedExercises = List.of(userExerciseWithMockedData());

        when(userService.getUserById(userId)).thenReturn(mock(User.class));
        when(userExerciseRepository.findAllByUserId(userId)).thenReturn(List.of());
        when(exerciseService.getAllExercises()).thenReturn(List.of(mock(Exercise.class)));
        when(userExerciseRepository.saveAll(anyList())).thenReturn(savedExercises);

        // when
        List<String> result = userExerciseService.syncUserExercise(userId);

        // then
        assertThat(result.size()).isEqualTo(savedExercises.size());
    }

    @Test
    void shouldReturnEmptyListWhenSyncUserExerciseAndExerciseListEmpty() {
        //given
        when(userExerciseRepository.findAllByUserId(userId)).thenReturn(List.of());
        when(exerciseService.getAllExercises()).thenReturn(List.of());

        //when
        List<String> result = userExerciseService.syncUserExercise(userId);

        //then
        assertThat(result).isEmpty();
        verify(userExerciseRepository, never()).saveAll(anyList());
    }

    @Test
    void shouldDisableUserExercisesWhenUpdateUserExerciseEnabledStatus() {
        //given
        final User user = mock(User.class);
        final List<String> disabledNames = List.of("front squat");
        when(userService.getUserById(user.getId())).thenReturn(user);

        //when
        userExerciseService.updateUserExerciseEnabledStatus(user.getId(), disabledNames);

        //then
        verify(userExerciseRepository).updateEnabledByUserIdAndNames(eq(user.getId()), eq(disabledNames), any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnUserExerciseResponseSliceWhenGetAllUserExercisesByUserId() {
        //given
        final PagingRequest pagingRequest = PagingRequest.builder().build();
        final UserExerciseFilterRequest filterRequest = UserExerciseFilterRequest.builder().build();

        final UserExercise userExercise1 = userExerciseWithMockedData();
        final UserExercise userExercise2 = userExerciseWithMockedData();
        final List<UserExercise> exerciseList = List.of(userExercise1, userExercise2);
        final Page<UserExercise> mockedPage = new PageImpl<>(exerciseList);

        when(userExerciseRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(mockedPage);

        //when
        Slice<UserExerciseResponse> result = userExerciseService.getAllUserExercisesByUserId(
                userId, pagingRequest, filterRequest);

        //then
        List<UserExerciseResponse> content = result.getContent();
        assertThat(content.getFirst().id()).isEqualTo(userExercise1.getId());
        assertThat(content.getLast().id()).isEqualTo(userExercise2.getId());
    }

    @Test
    void shouldReturnUserExerciseResponseWhenGetUserExerciseByUserIdAndExerciseName() {
        //given
        final String exerciseName = "back squat";
        final UserExercise userExercise = userExerciseWithMockedData();

        when(userExercise.getExercise().getName()).thenReturn(exerciseName);
        when(userExerciseRepository.findByUserIdAndExerciseName(userId, exerciseName))
                .thenReturn(Optional.of(userExercise));

        //when
        UserExerciseResponse result = userExerciseService.getUserExerciseByUserIdAndExerciseName(userId, exerciseName);

        //then
        assertThat(result.exerciseName()).isEqualTo(exerciseName);
    }

    @Test
    void shouldEnableAllExercisesWhenUpdateUserExerciseEnabledStatusAndDisabledExercisesEmpty() {
        //given
        final List<String> disabledNames = List.of();

        //when
        userExerciseService.updateUserExerciseEnabledStatus(userId, disabledNames);

        //then
        verify(userExerciseRepository).enableAllByUserId(eq(userId), any());
    }

    @Test
    void shouldReturnEnabledUserExercisesByMuscleGroupByUserId() {
        //given
        final UserExercise userExercise1 = mock(UserExercise.class);
        final UserExercise userExercise2 = mock(UserExercise.class);

        when(userExercise1.getExercise()).thenReturn(mock(Exercise.class));
        when(userExercise2.getExercise()).thenReturn(mock(Exercise.class));
        when(userExercise1.getExercise().getMuscleGroup()).thenReturn(MuscleGroup.ABS);
        when(userExercise2.getExercise().getMuscleGroup()).thenReturn(MuscleGroup.QUADS);
        when(userExerciseRepository.findAllByUserIdAndEnabledIsTrue(userId))
                .thenReturn(List.of(userExercise1, userExercise2));

        final Map<MuscleGroup, List<UserExercise>> expectedMap = Map.of(MuscleGroup.ABS, List.of(userExercise1),
                                                                        MuscleGroup.QUADS, List.of(userExercise2));
        //when
        Map<MuscleGroup, List<UserExercise>> result = userExerciseService.getEnabledUserExercisesByMuscleGroup(userId);

        //then
        assertThat(result).isEqualTo(expectedMap);
    }

    @Test
    void shouldReturnAllExerciseNamesByUserId() {
        //given
        when(userExerciseRepository.findAllExerciseNamesByUserId(userId)).thenReturn(List.of("pull up", "push up"));

        //when
        List<String> result = userExerciseService.getAllExerciseNames(userId);

        //then
        assertThat(result).containsExactlyInAnyOrder("pull up", "push up");
    }

    @Test
    void shouldThrowUserExerciseNotFoundExceptionWhenGetUserExerciseByUserIdAndExerciseName() {
        //given
        final String exerciseName = "back squat";
        when(userExerciseRepository.findByUserIdAndExerciseName(userId, exerciseName)).thenReturn(Optional.empty());

        //when && then
        assertThatThrownBy(() -> userExerciseService.getUserExerciseByUserIdAndExerciseName(userId, exerciseName))
                .isInstanceOf(UserExerciseException.class)
                .hasMessage(ExceptionType.USER_EXERCISE_NOT_FOUND.getMessage());
    }

    @Test
    void shouldThrowUserExerciseAlreadyExistsExceptionWhenSyncUserExercise() {
        //given
        final UserExercise userExercise = mock(UserExercise.class);
        final Instant lastModifiedDate = Instant.now();
        final List<UserExercise> userExercises = List.of(userExercise);
        when(userExercise.getModifiedAt()).thenReturn(lastModifiedDate);
        when(userExerciseRepository.findAllByUserId(userId)).thenReturn(userExercises);
        when(exerciseService.getExercisesByCreatedAtSince(lastModifiedDate)).thenReturn(List.of(mock(Exercise.class)));
        when(userExerciseRepository.saveAll(anyList())).thenThrow(new DataIntegrityViolationException("violation"));

        //when && then
        assertThatThrownBy(() -> userExerciseService.syncUserExercise(userId))
                .isInstanceOf(UserExerciseException.class)
                .hasMessage(ExceptionType.USER_EXERCISE_ALREADY_EXISTS.getMessage());
    }

    private static UserExercise userExerciseWithMockedData() {
        return new UserExercise(mock(User.class), mock(Exercise.class));
    }
}
