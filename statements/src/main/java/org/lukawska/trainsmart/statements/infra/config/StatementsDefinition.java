package org.lukawska.trainsmart.statements.infra.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
@ConfigurationProperties
@NoArgsConstructor
@Getter
@Setter
@Slf4j
public class StatementsDefinition {

    private Map<String, Statement> statements = new HashMap<>();

    public Optional<Statement> findStatementByCode(String code) {
        return Optional.ofNullable(statements.get(code));
    }

    public Map<String, Statement> getRequiredStatementsMap() {
        return statements.entrySet()
                         .stream()
                         .filter(s -> s.getValue().required())
                         .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @PostConstruct
    public void validateMapping() {
        if (statements == null || statements.isEmpty()) {
            log.warn("No statements configured under 'statements' prefix");
            return;
        }

        List<String> invalidKeys = statements.entrySet().stream()
                                             .filter(e -> e.getKey() == null || e.getKey()
                                                                                 .isBlank() || e.getValue() == null)
                                             .map(Map.Entry::getKey)
                                             .toList();

        if (!invalidKeys.isEmpty()) {
            throw new IllegalStateException("Invalid statements configuration for keys: " + invalidKeys);
        }

        log.info("Loaded {} statement(s) from configuration", statements.size());
    }
}
