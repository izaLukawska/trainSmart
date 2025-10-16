package org.lukawska.trainsmart.statements.application.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.statements.application.exception.StatementAcceptanceRequired;
import org.lukawska.trainsmart.statements.application.exception.StatementNotFound;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.lukawska.trainsmart.statements.domain.repositories.UserAgreementRepository;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;
import org.lukawska.trainsmart.statements.infra.config.Statement;
import org.lukawska.trainsmart.statements.infra.config.StatementsDefinition;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAgreementService {

	private final UserAgreementRepository repository;

	private final StatementsDefinition definitions;

	@Transactional
	public UserAgreement signAgreement(Long userId, String statementCode, AgreementStatus decisionStatus) {
		Objects.requireNonNull(decisionStatus, "Status cannot be null");
		Statement statement = definitions.findStatementByCode(statementCode)
		                                 .orElseThrow(() -> new StatementNotFound("Statement not found"));

		if (statement.required() && decisionStatus == AgreementStatus.REJECTED) {
			throw new StatementAcceptanceRequired("Statement acceptance is required");
		}

		UserAgreement agreement = repository.findByUserIdAndStatementCode(userId, statementCode)
		                                     .orElseGet(() -> repository.save(new UserAgreement(userId,
		                                                                                        statementCode,
		                                                                                        statement.version(),
		                                                                                        decisionStatus)));

		if (!shouldSkipUpdate(agreement, decisionStatus, statement)) {
			agreement.setStatementVersion(statement.version());
			agreement.setStatus(decisionStatus);
			return repository.save(agreement);
		}

		return agreement;
	}

	public List<String> findRequiredStatementsToSign(Long userId) {
		Map<String, UserAgreement> userAgreements = getUserAgreementsByCode(userId);
		return definitions.getRequiredStatementsMap().entrySet().stream()
		                  .filter(entry -> {
			                  String code = entry.getKey();
			                  Statement statement = entry.getValue();
			                  UserAgreement agreement = userAgreements.get(code);
			                  return agreement == null || agreement.getStatementVersion() != statement.version();
		                  })
		                  .map(Map.Entry::getKey)
		                  .collect(Collectors.toList());
	}

	private Map<String, UserAgreement> getUserAgreementsByCode(Long userId) {
		return repository.findAllByUserId(userId).stream()
		                 .collect(Collectors.toMap(UserAgreement::getStatementCode, Function.identity()));
	}


	private boolean shouldSkipUpdate(UserAgreement agreement, AgreementStatus newStatus, Statement statement) {
		boolean required = statement.required();
		boolean statusChanged = !agreement.getStatus().equals(newStatus);

		return !required && !statusChanged;
	}
}
