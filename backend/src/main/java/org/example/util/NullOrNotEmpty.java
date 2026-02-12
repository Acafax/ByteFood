package org.example.util;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

/**
 * Validation annotation for PATCH DTOs where a collection field can be null (not provided),
 * but if provided, must not be empty.
 * 
 * Use case: Partial updates where omitting a field means "don't change",
 * but sending an empty list is invalid.
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NullOrNotEmptyValidator.class)
public @interface NullOrNotEmpty {
    String message() default "must not be empty if provided";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

class NullOrNotEmptyValidator implements ConstraintValidator<NullOrNotEmpty, Collection<?>> {
    @Override
    public boolean isValid(Collection<?> value, ConstraintValidatorContext context) {
        // if null -> OK (passes) - field was not provided
        if (value == null) {
            return true;
        }
        // if not null -> check if collection is not empty
        return !value.isEmpty();
    }
}

