package org.lukawska.trainsmart.health_survey.application.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class AdultAgeValidator implements ConstraintValidator<AdultAge, LocalDate> {

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        if (birthDate == null) {
            return true;
        }

        LocalDate cutoffDate = LocalDate.now().minusYears(18);

        return birthDate.isBefore(cutoffDate) || birthDate.isEqual(cutoffDate);
    }
}
