package org.lukawska.trainsmart.statements.testutil;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.statements.infra.config.Statement;

import java.util.Random;
import java.util.UUID;

@UtilityClass
public final class StatementTestData {

    public static Statement requiredStatement(int version) {
        return new Statement(randomStatementCode(), version, true, randomStatementCode());
    }

    public static Statement requiredStatement() {
        return new Statement(randomStatementCode(), new Random().nextInt(10), true, randomStatementCode());
    }

    public static Statement optionalStatement() {
        return new Statement(randomStatementCode(), new Random().nextInt(10), false, randomStatementCode());
    }

    public static String randomStatementCode() {
        return UUID.randomUUID().toString();
    }
}
