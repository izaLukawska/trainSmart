package org.lukawska.trainsmart.statements.infra.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@ConfigurationProperties
@Getter
@Setter
@Slf4j
public class StatementsDefinition {

    private final Map<String, Statement> statements = new HashMap<>();

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
        if (statements.isEmpty()) {
            log.warn("No statements configured under 'statements' prefix");
            return;
        }

        List<String> invalidKeys = statements.entrySet()
                                             .stream()
                                             .filter(e -> StringUtils.isBlank(e.getKey()) || e.getValue() == null)
                                             .map(Map.Entry::getKey)
                                             .toList();

        if (!invalidKeys.isEmpty()) {
            throw new IllegalStateException("Invalid statements configuration for keys: " + invalidKeys);
        }
    }
}
