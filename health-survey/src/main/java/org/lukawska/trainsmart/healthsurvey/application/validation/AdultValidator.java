package org.lukawska.trainsmart.healthsurvey.application.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class AdultValidator implements ConstraintValidator<Adult, LocalDate> {

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        if (birthDate == null) {
            return true;
        }

        LocalDate cutoffDate = LocalDate.now().minusYears(18);

        return !birthDate.isAfter(cutoffDate);
    }
}
