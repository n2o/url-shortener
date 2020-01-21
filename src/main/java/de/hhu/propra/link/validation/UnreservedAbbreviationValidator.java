package de.hhu.propra.link.validation;

import de.hhu.propra.link.services.AbbreviationService;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class UnreservedAbbreviationValidator implements ConstraintValidator<UnreservedAbbreviation, String> {

    private final AbbreviationService abbreviationService;

    public UnreservedAbbreviationValidator(AbbreviationService abbreviationService) {
        this.abbreviationService = abbreviationService;
    }

    @Override
    public boolean isValid(String abbreviation, ConstraintValidatorContext context) {
        return !abbreviationService.isReserved(abbreviation);
    }
}
