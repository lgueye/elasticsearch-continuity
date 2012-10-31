/**
 *
 */
package org.diveintojee.poc.domain.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * @author louis.gueye@gmail.com
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ValidEmailValidator.class)
public @interface ValidEmail {
    String message() default "{account.email.valid.format.required}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
