package org.lukawska.trainsmart.statements.application.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementRequest;
import org.lukawska.trainsmart.statements.application.exception.ExceptionType;
import org.lukawska.trainsmart.statements.application.exception.StatementException;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.lukawska.trainsmart.statements.domain.repositories.UserAgreementRepository;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;
import org.lukawska.trainsmart.statements.infra.config.Statement;
import org.lukawska.trainsmart.statements.infra.config.StatementsDefinition;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserAgreementValidator {

    private final StatementsDefinition statementsDefinition;

    private final UserAgreementRepository userAgreementRepository;

    public Statement validateUserAgreement(UserAgreementRequest request) {
        log.info("Checking if statement with code {} exists", request.statementCode());
        Statement statement = getStatement(request);

        log.debug("Validating that required statement: {} is accepted", statement);
        validateRequiredAcceptance(statement, request);

        return statement;
    }

    public Statement validateNewUserAgreement(UserAgreementRequest request) {
        Statement statement = validateUserAgreement(request);

        log.debug("Checking if there is no such user agreement: {}", request);
        validateNoDuplicateUserAgreement(request);

        return statement;
    }

    public boolean outdatedUserAgreement(UserAgreement userAgreement) {
        Statement required = statementsDefinition.getRequiredStatementsMap()
                                                 .get(userAgreement.getStatementCode());

        return required != null && userAgreement.getStatementVersion() != required.version();
    }

    private Statement getStatement(UserAgreementRequest request) {
        log.debug("Fetching data for statement with code: {}", request.statementCode());
        return statementsDefinition.findStatementByCode(request.statementCode())
                                   .orElseThrow(() -> new StatementException(ExceptionType.STATEMENT_NOT_FOUND));
    }

    private void validateRequiredAcceptance(Statement statement, UserAgreementRequest request) {
        log.debug("Checking if required statement with code: {} is accepted", request.statementCode());
        if (statement.required() && request.agreementStatus() == AgreementStatus.REJECTED) {
            throw new StatementException(ExceptionType.STATEMENT_ACCEPTANCE_REQUIRED);
        }
    }

    private void validateNoDuplicateUserAgreement(UserAgreementRequest request) {
        if (userAgreementRepository.findByUserIdAndStatementCode(request.userId(), request.statementCode())
                                   .isPresent()) {
            throw new StatementException(ExceptionType.USER_AGREEMENT_ALREADY_EXISTS);
        }
    }
}
