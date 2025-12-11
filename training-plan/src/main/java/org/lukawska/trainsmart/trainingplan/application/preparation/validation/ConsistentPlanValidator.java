package org.lukawska.trainsmart.trainingplan.application.preparation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanRequest;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.WeekDay;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class ConsistentPlanValidator implements ConstraintValidator<ConsistentPlanRequest, TrainingPlanRequest> {

    @Override
    public boolean isValid(TrainingPlanRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }

        int daysPerWeek = request.daysPerWeek();
        List<WeekDay> preferredDays = request.preferredDays();
        if (!CollectionUtils.isEmpty(preferredDays) && preferredDays.size() != daysPerWeek) {
            String message = String.format("Preferred days count (%d) does not match given days per week (%d).",
                                           preferredDays.size(), daysPerWeek);
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();

            return false;
        }

        return true;
    }
}
