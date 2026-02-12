package org.example.util;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.RecordComponent;

/**
 * Validation annotation that ensures at least one field in a record is not null.
 * Used for PATCH DTOs where sending an empty body should be rejected.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AtLeastOneNotNullValidator.class)
public @interface AtLeastOneNotNull {
    String message() default "At least one field must be provided";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

class AtLeastOneNotNullValidator implements ConstraintValidator<AtLeastOneNotNull, Record> {

    @Override
    public boolean isValid(Record value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        try {
            for (RecordComponent component : value.getClass().getRecordComponents()) {
                Object fieldValue = component.getAccessor().invoke(value);
                if (fieldValue != null) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }
}

