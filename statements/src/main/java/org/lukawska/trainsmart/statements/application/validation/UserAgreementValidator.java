package org.lukawska.trainsmart.statements.application.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementRequest;
import org.lukawska.trainsmart.statements.application.exception.ExceptionType;
import org.lukawska.trainsmart.statements.application.exception.StatementException;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;
import org.lukawska.trainsmart.statements.infra.config.Statement;
import org.lukawska.trainsmart.statements.infra.config.StatementsDefinition;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserAgreementValidator {

    private final StatementsDefinition statementsDefinition;

    public Statement validateUserAgreement(UserAgreementRequest request) {
        Statement statement = getStatement(request);
        validateRequiredAcceptance(statement, request);

        return statement;
    }

    public boolean outdatedUserAgreement(UserAgreement userAgreement) {
        Statement requiredStatement = statementsDefinition.getRequiredStatementsMap()
                                                          .get(userAgreement.getStatementCode());

        return requiredStatement != null && userAgreement.getStatementVersion() != requiredStatement.version();
    }

    private Statement getStatement(UserAgreementRequest request) {
        return statementsDefinition.findStatementByCode(request.statementCode())
                                   .orElseThrow(() -> new StatementException(ExceptionType.STATEMENT_NOT_FOUND));
    }

    private void validateRequiredAcceptance(Statement statement, UserAgreementRequest request) {
        if (statement.required() && request.agreementStatus() == AgreementStatus.REJECTED) {
            throw new StatementException(ExceptionType.STATEMENT_ACCEPTANCE_REQUIRED);
        }
    }
}
