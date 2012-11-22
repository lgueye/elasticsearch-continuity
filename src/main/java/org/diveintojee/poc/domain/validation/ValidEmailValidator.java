/**
 *
 */
package org.diveintojee.poc.domain.validation;

import com.google.common.base.Strings;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * @author louis.gueye@gmail.com
 */
public class ValidEmailValidator implements ConstraintValidator<ValidEmail, String> {

    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /**
     * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
     */
    @Override
    public void initialize(ValidEmail constraintAnnotation) {
    }

    /**
     * @see javax.validation.ConstraintValidator#isValid(Object,
     *      javax.validation.ConstraintValidatorContext)
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (Strings.isNullOrEmpty(value)) return true;

        return Pattern.matches(EMAIL_PATTERN, value);

    }

}
