package de.hhu.propra.link.validation;

import de.hhu.propra.link.util.UrlUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class HttpUrlValidator implements ConstraintValidator<HttpUrl, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Emptiness is the concern of @NotEmpty; an empty value passes this constraint.
        return value == null || value.isBlank() || UrlUtil.isHttpUrl(value);
    }
}
