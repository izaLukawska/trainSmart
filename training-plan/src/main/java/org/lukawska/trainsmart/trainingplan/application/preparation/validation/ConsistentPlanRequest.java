package org.lukawska.trainsmart.trainingplan.application.preparation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ConsistentPlanValidator.class)
public @interface ConsistentPlanRequest {

    String message() default "(If present) preferred days count must match chosen days per week count";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
