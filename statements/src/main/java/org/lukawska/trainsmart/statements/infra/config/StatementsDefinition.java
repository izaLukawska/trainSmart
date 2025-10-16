package org.lukawska.trainsmart.statements.infra.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@ConfigurationProperties(prefix = "")
@NoArgsConstructor
@Getter
@Setter
public class StatementsDefinition {

	private Map<String, Statement> statements = new HashMap<>();

	public Optional<Statement> findStatementByCode(String code) {
		return Optional.ofNullable(statements.get(code));
	}

	public Map<String, Statement> getRequiredStatementsMap() {
		return statements.entrySet().stream()
		                 .filter(s -> s.getValue().required())
		                 .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
}
