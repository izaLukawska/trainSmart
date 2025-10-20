package org.lukawska.trainsmart.statements.application.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementRequest;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementResponse;
import org.lukawska.trainsmart.statements.application.exception.ExceptionType;
import org.lukawska.trainsmart.statements.application.exception.StatementException;
import org.lukawska.trainsmart.statements.application.mapper.UserAgreementMapper;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.lukawska.trainsmart.statements.domain.repositories.UserAgreementRepository;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;
import org.lukawska.trainsmart.statements.infra.config.Statement;
import org.lukawska.trainsmart.statements.infra.config.StatementsDefinition;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAgreementService {

	private final UserAgreementRepository repository;

	private final StatementsDefinition definitions;

	private final UserAgreementMapper mapper;

	@Transactional
	public UserAgreementResponse signAgreement(UserAgreementRequest request) {
		log.info("Fetching statement with code: {}", request.statementCode());
		Statement statement = definitions.findStatementByCode(request.statementCode())
		                                 .orElseThrow(() -> new StatementException(ExceptionType.STATEMENT_NOT_FOUND));

		if (statement.required() && request.status() == AgreementStatus.REJECTED) {
			throw new StatementException(ExceptionType.STATEMENT_ACCEPTANCE_REQUIRED);
		}

		Optional<UserAgreement> found = repository.findByUserIdAndStatementCode(request.userId(),
		                                                                        request.statementCode());
		if (found.isEmpty()) {
			log.info("No user found for id {} and code {}. Creating and saving new agreement.",
			         request.userId(), request.statementCode());
			UserAgreement userAgreement = mapper.mapToEntity(request);
			return mapper.mapToResponse(repository.save(userAgreement));
		}

		log.info("User with id {} and code {} exists. Checking if update is needed...",
		         request.userId(), request.statementCode());
		UserAgreement agreement = found.get();
		if (!shouldSkipUpdate(agreement, request.status(), statement)) {
			log.info("Updating record for user with id {} and statement {}",
			         request.userId(), request.statementCode());
			agreement.updateStatementVersion(statement.version());
			agreement.changeStatus(request.status());
			log.info("Updating changes for agreement code: {} version {} with new status {}",
			         agreement.getStatementCode(), agreement.getStatementVersion(), agreement.getStatus());
			UserAgreement saved = repository.save(agreement);
			return mapper.mapToResponse(saved);
		}

		log.info("No changes required. Returning existing record with id: {}", agreement.getId());
		return mapper.mapToResponse(agreement);
	}

	public List<UserAgreementResponse> getRequiredStatementsToSign(Long userId) {
		log.info("Getting required statements to sign for userId: {}", userId);
		List<UserAgreement> userAgreements = repository.findAllByUserId(userId);

		if (userAgreements.isEmpty()) {
			throw new StatementException(ExceptionType.USER_NOT_FOUND);
		}

		List<UserAgreementResponse> found = userAgreements.stream()
		                                                  .filter(ua -> {
			                                                  Statement required =
					                                                  definitions.getRequiredStatementsMap()
					                                                             .get(ua.getStatementCode());
			                                                  return required != null &&
					                                                  ua.getStatementVersion() != required.version();
		                                                  })
		                                                  .map(mapper::mapToResponse)
		                                                  .collect(Collectors.toList());

		log.info("Found required statements to sign count: {} for user with ID: {}", found.size(), userId);
		return found;
	}

	private boolean shouldSkipUpdate(UserAgreement agreement, AgreementStatus newStatus, Statement statement) {
		boolean required = statement.required();
		boolean statusChanged = !agreement.getStatus().equals(newStatus);

		return !required && !statusChanged;
	}
}
