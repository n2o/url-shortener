package de.hhu.propra.link.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = UnreservedAbbreviationValidator.class)
@Target(FIELD)
@Retention(RUNTIME)
public @interface UnreservedAbbreviation {

    String message() default "the abbreviation \"${validatedValue}\" is reserved";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
