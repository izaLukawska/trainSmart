package org.lukawska.trainsmart.healthsurvey.presentation.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import lombok.experimental.UtilityClass;

import java.util.UUID;

import static org.mockito.Mockito.*;

@UtilityClass
class ExceptionTestData {

    public static ConstraintViolation<?> mockViolation(String message) {
        Path path = mock(Path.class);
        doReturn(UUID.randomUUID().toString()).when(path).toString();
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn(message);

        return violation;
    }
}
