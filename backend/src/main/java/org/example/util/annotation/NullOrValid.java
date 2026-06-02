package org.example.util.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;

/*
    For PatchDTO where field can be null/blank so standard annotation @NotBlank is useless,
    because if user won't give variable, with change to null server trigger @NotBland annotation so server will return BadRequest
    for instance if manager wanna to change name of product he give this variable in request body, rest of variables are null, and it's trigger @NotBlank
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {NullOrNotBlankValidator.class, NullOrGreaterThanZero.class, NullOrPositiveOrZero.class})
public @interface NullOrValid {
    String message() default "must not be blank";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

class NullOrNotBlankValidator implements ConstraintValidator<NullOrValid, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // if null -> OK (passes)
        if (value == null) {
            return true;
        }
        // if not null -> check if variable is not blank
        return !value.isBlank();
    }

}

class NullOrGreaterThanZero implements ConstraintValidator<NullOrValid, Long>{

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value==null){
            return true;
        }

        boolean isValid = value > 0L;

        if (!isValid) {
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate("must be greater than 0")
                    .addConstraintViolation();
        }

        return isValid;
    }


}

class NullOrPositiveOrZero implements ConstraintValidator<NullOrValid, BigDecimal> {

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        // if null -> OK (passes) - field was not provided in patch
        if (value == null) {
            return true;
        }

        boolean isValid = value.compareTo(BigDecimal.ZERO) >= 0;

        if (!isValid) {
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate("must be zero or positive")
                    .addConstraintViolation();
        }

        return isValid;
    }
}