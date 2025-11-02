package org.lukawska.trainsmart.health_survey.application.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AdultAgeValidator.class)
public @interface AdultAge {

    String message() default "Age must be at least 18.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
