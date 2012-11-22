/**
 *
 */
package org.diveintojee.poc.domain.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

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
