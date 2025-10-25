package org.lukawska.trainsmart.statements.presentation.controller;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.lukawska.trainsmart.statements.testutil.StatementTestData.randomStatementCode;
import static org.lukawska.trainsmart.statements.testutil.UserAgreementTestData.randomUserAgreementRequest;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAgreementControllerTest {

    @Mock
    private UserAgreementService service;

    @InjectMocks
    private UserAgreementController controller;

    @Test
    void shouldReturnResponseWhenSignNewAgreementSuccess() {
        //given
        UserAgreementRequest request = randomUserAgreementRequest();
        UserAgreementResponse expectedResponse = new UserAgreementResponse(null,
                                                                           request.userId(),
                                                                           request.statementCode(),
                                                                           2,
                                                                           AgreementStatus.ACCEPTED);
        when(service.signNewAgreement(request)).thenReturn(expectedResponse);

        //when
        ResponseEntity<UserAgreementResponse> responseEntity = controller.signNewAgreement(request);

        //then
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    void shouldReturnResponseWhenReSignAgreementSuccess() {
        // given
        UserAgreementRequest request = randomUserAgreementRequest();
        UserAgreementResponse expectedResponse = new UserAgreementResponse(null,
                                                                           request.userId(),
                                                                           request.statementCode(),
                                                                           2,
                                                                           AgreementStatus.ACCEPTED);
        when(service.reSignAgreement(request)).thenReturn(expectedResponse);

        // when
        ResponseEntity<UserAgreementResponse> responseEntity = controller.reSignAgreement(request);

        // then
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);
    }


    @Test
    void shouldReturnRequiredStatementsListWhenFound() {
        //given
        final Long userId = new Random().nextLong(10);
        UserAgreementResponse response = new UserAgreementResponse(null,
                                                                   userId,
                                                                   randomStatementCode(),
                                                                   2,
                                                                   AgreementStatus.ACCEPTED);

        when(service.getRequiredStatementsToSign(userId)).thenReturn(List.of(response));

        //when
        List<UserAgreementResponse> actualList = controller.getRequiredStatementsToSign(userId);

        //then
        assertThat(actualList).hasSize(1)
                              .contains(response);
    }

    @Test
    void shouldReturnEmptyRequiredStatementsList() {
        //given
        final Long userId = new Random().nextLong(10);
        when(service.getRequiredStatementsToSign(userId)).thenReturn(List.of());

        //when
        List<UserAgreementResponse> actualList = controller.getRequiredStatementsToSign(userId);

        //then
        assertThat(actualList).isEmpty();
    }
}
