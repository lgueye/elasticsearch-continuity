/*
 *
 */
package org.diveintojee.poc.business.impl;

import com.google.common.base.Preconditions;

import org.apache.commons.collections.CollectionUtils;
import org.diveintojee.poc.domain.AbstractEntity;
import org.diveintojee.poc.domain.business.Validator;
import org.diveintojee.poc.domain.validation.ValidationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * @author louis.gueye@gmail.com
 */
@Component(value = ValidatorImpl.BEAN_ID)
public class ValidatorImpl implements Validator {

    public static final String BEAN_ID = "org.diveintojee.poc.business.impl.Validator";

    @Autowired
    private javax.validation.Validator validator;

    @Override
    public <T extends AbstractEntity> void validate(final T type, final ValidationContext context) {

        Preconditions.checkArgument(type != null, "Illegal call to validate, object is required");

        Preconditions.checkArgument(context != null, "Illegal call to validate, validation context is required");

        final Set<ConstraintViolation<T>> constraintViolations = validator.validate(type, context.getContext());

        if (CollectionUtils.isEmpty(constraintViolations))
            return;

        final Set<ConstraintViolation<?>> propagatedViolations = new HashSet<ConstraintViolation<?>>(
                constraintViolations.size());

        for (final ConstraintViolation<?> violation : constraintViolations) {

            propagatedViolations.add(violation);

        }

        throw new ConstraintViolationException(propagatedViolations);

    }

}
