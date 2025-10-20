package org.lukawska.trainsmart.statements.infra.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class StatementsDefinitionTest {

	@Test
	void shouldReturnStatementWhenPresent() {
		//given
		StatementsDefinition def = new StatementsDefinition();
		final String statementCode = UUID.randomUUID().toString();
		Statement statement = new Statement(statementCode, new Random().nextInt(), new Random().nextBoolean(),
		                                    UUID.randomUUID()
		                                        .toString());
		Map<String, Statement> defMap = Map.of(statementCode, statement);
		def.setStatements(defMap);

		//when
		Optional<Statement> found = def.findStatementByCode(statementCode);

		//then
		assertThat(found).isPresent().contains(statement);
	}

	@Test
	void shouldReturnEmptyStatementWhenStatementNotFound() {
		//given
		StatementsDefinition def = new StatementsDefinition();
		final String statementCode = UUID.randomUUID().toString();
		Statement statement = new Statement(statementCode, new Random().nextInt(), new Random().nextBoolean(),
		                                    UUID.randomUUID()
		                                        .toString());
		Map<String, Statement> defMap = Map.of(statementCode, statement);
		def.setStatements(defMap);

		//when
		Optional<Statement> notFound = def.findStatementByCode(UUID.randomUUID().toString());

		//then
		assertThat(notFound).isEmpty();
	}

	@Test
	void shouldReturnOnlyRequiredStatementsMap() {
		// given
		StatementsDefinition def = new StatementsDefinition();
		final String requiredStatementCode = UUID.randomUUID().toString();
		final String optionalStatementCode = UUID.randomUUID().toString();
		final Statement requiredStatement = new Statement(requiredStatementCode, new Random().nextInt(), true,
		                                                  UUID.randomUUID()
		                                                      .toString());
		final Statement optionalStatement = new Statement(optionalStatementCode, new Random().nextInt(), false,
		                                                  UUID.randomUUID()
		                                                      .toString());
		Map<String, Statement> defMap = Map.of(requiredStatementCode, requiredStatement, optionalStatementCode,
		                                       optionalStatement);
		def.setStatements(defMap);

		// when
		Map<String, Statement> required = def.getRequiredStatementsMap();

		// then
		assertThat(required).hasSize(1)
		                    .containsEntry(requiredStatementCode, requiredStatement)
		                    .doesNotContainKey(optionalStatementCode);
	}

	@Test
	void shouldReturnEmptyRequiredStatementsMapWhenNoRequiredStatementsPresent() {
		// given
		StatementsDefinition def = new StatementsDefinition();
		final String optionalStatementCode = UUID.randomUUID().toString();
		Statement optionalStatement = new Statement(optionalStatementCode, new Random().nextInt(), false,
		                                            UUID.randomUUID()
		                                                .toString());
		Map<String, Statement> defMap = Map.of(optionalStatementCode, optionalStatement);
		def.setStatements(defMap);

		//when && then
		assertThat(def.getRequiredStatementsMap()).isEmpty();
	}
}
