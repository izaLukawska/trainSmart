package org.lukawska.trainsmart.statements.testutil;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import lombok.experimental.UtilityClass;
import org.springframework.validation.FieldError;

import java.util.UUID;

import static org.mockito.Mockito.*;

@UtilityClass
public class ExceptionTestData {

    public static FieldError randomFieldError() {
        return new FieldError(randomText(), randomText(), randomText());
    }

    public static String randomText() {
        return UUID.randomUUID().toString();
    }

    public static ConstraintViolation<?> mockViolation(String message) {
        Path path = mock(Path.class);
        doReturn(UUID.randomUUID().toString()).when(path).toString();
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn(message);
        return violation;
    }
}
