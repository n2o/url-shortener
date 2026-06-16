package de.hhu.propra.link.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validates that the annotated string is a well-formed absolute URL using the {@code http} or
 * {@code https} scheme.
 *
 * <p>This deliberately rejects every other scheme — notably {@code javascript:}, {@code data:} and
 * {@code file:} — so that a stored short link can never be used to smuggle a script or local-file
 * URL into a redirect. Emptiness is not this constraint's concern; combine it with
 * {@link jakarta.validation.constraints.NotEmpty} if a value is required.
 */
@Documented
@Constraint(validatedBy = HttpUrlValidator.class)
@Target(FIELD)
@Retention(RUNTIME)
public @interface HttpUrl {

    String message() default "must be a valid http or https URL";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
