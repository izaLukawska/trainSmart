package org.lukawska.trainsmart.statements.presentation.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementRequest;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementResponse;
import org.lukawska.trainsmart.statements.application.services.UserAgreementService;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAgreementControllerTest {

    @Mock
    private UserAgreementService service;

    @InjectMocks
    private UserAgreementController controller;

    private Long userId;

    private UserAgreementResponse expectedResponse;

    @BeforeEach
    void setUp() {
        userId = new Random().nextLong(10);
        expectedResponse = mock(UserAgreementResponse.class);
    }

    @Test
    void shouldReturnResponseWhenSignNewAgreementSuccess() {
        //given
        UserAgreementRequest request = new UserAgreementRequest(userId,
                                                                UUID.randomUUID().toString(),
                                                                AgreementStatus.ACCEPTED);

        when(service.signNewAgreement(request)).thenReturn(expectedResponse);

        //when
        ResponseEntity<UserAgreementResponse> responseEntity = controller.signNewAgreement(request);

        //then
        assertThat(responseEntity.getBody()).isNotNull().isEqualTo(expectedResponse);
    }

    @Test
    void shouldReturnResponseWhenReSignAgreementSuccess() {
        // given
        UserAgreementRequest request = new UserAgreementRequest(userId,
                                                                UUID.randomUUID().toString(),
                                                                AgreementStatus.REJECTED);

        when(service.reSignAgreement(request)).thenReturn(expectedResponse);

        // when
        ResponseEntity<UserAgreementResponse> responseEntity = controller.reSignAgreement(request);

        // then
        assertThat(responseEntity.getBody()).isNotNull().isEqualTo(expectedResponse);
    }


    @Test
    void shouldReturnRequiredStatementsListWhenFound() {
        //given
        List<UserAgreementResponse> expectedList = List.of(expectedResponse);
        when(service.getRequiredStatementsToSign(userId)).thenReturn(expectedList);

        //when
        List<UserAgreementResponse> actualList = controller.getRequiredStatementsToSign(userId);

        //then
        assertThat(expectedList.equals(actualList));
    }

    @Test
    void shouldReturnEmptyRequiredStatementsList() {
        //given
        when(service.getRequiredStatementsToSign(userId)).thenReturn(List.of());

        //when
        List<UserAgreementResponse> actualList = controller.getRequiredStatementsToSign(userId);

        //then
        assertThat(actualList).isEmpty();
    }
}
