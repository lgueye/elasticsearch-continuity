/*
 *
 */
package org.diveintojee.poc.business;

import org.diveintojee.poc.domain.Classified;
import org.diveintojee.poc.domain.business.Validator;
import org.diveintojee.poc.domain.validation.PreModifyValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.diveintojee.poc.business.impl.ValidatorImpl;
import org.diveintojee.poc.domain.AbstractEntity;
import org.diveintojee.poc.domain.business.Validator;
import org.diveintojee.poc.domain.validation.ValidationContext;

/**
 * @author louis.gueye@gmail.com
 */
@RunWith(MockitoJUnitRunner.class)
public class ValidatorImplTest {

    @Mock
    private javax.validation.Validator validator;

    @InjectMocks
    private final Validator underTest = new PreModifyValidator();

    /**
     * Test method for
     * {@link org.diveintojee.poc.business.impl.ValidatorImpl#validate(org.diveintojee.poc.domain.AbstractEntity, org.diveintojee.poc.domain.validation.ValidationContext)}
     * .
     */
    @Test
    public final void validateShouldNotThrowConstraintViolationWithEmptyViolations() {

        // Variables
        AbstractEntity type;
        ValidationContext context;
        Set<ConstraintViolation<AbstractEntity>> violations;

        // Given
        type = Mockito.mock(Classified.class);
        context = ValidationContext.UPDATE;
        violations = null;
        Mockito.when(validator.validate(type, context.getContext())).thenReturn(violations);

        // When
        underTest.validate(type, context);

        // Then
        Mockito.verify(validator).validate(type, context.getContext());
        Mockito.verifyNoMoreInteractions(validator);

        // Given
        type = Mockito.mock(Classified.class);
        context = ValidationContext.UPDATE;
        violations = new HashSet<ConstraintViolation<AbstractEntity>>();
        Mockito.when(validator.validate(type, context.getContext())).thenReturn(violations);

        // When
        underTest.validate(type, context);

        // Then
        Mockito.verify(validator).validate(type, context.getContext());
        Mockito.verifyNoMoreInteractions(validator);

    }

    /**
     * Test method for
     * {@link org.diveintojee.poc.business.impl.ValidatorImpl#validate(org.diveintojee.poc.domain.AbstractEntity, org.diveintojee.poc.domain.validation.ValidationContext)}
     * .
     */
    @SuppressWarnings("unchecked")
    @Test(expected = ConstraintViolationException.class)
    public final void validateShouldThrowConstraintViolationWithNonEmptyViolations() {

        // Variables
        AbstractEntity type;
        ValidationContext context;
        Set<ConstraintViolation<AbstractEntity>> violations;

        // Given
        type = Mockito.mock(Classified.class);
        context = ValidationContext.UPDATE;
        violations = new HashSet<ConstraintViolation<AbstractEntity>>();
        violations.add(Mockito.mock(ConstraintViolation.class));
        Mockito.when(validator.validate(type, context.getContext())).thenReturn(violations);

        // When
        underTest.validate(type, context);

        // Then
        Mockito.verify(validator).validate(type, context.getContext());
        Mockito.verifyNoMoreInteractions(validator);

    }

    /**
     * Test method for
     * {@link org.diveintojee.poc.business.impl.ValidatorImpl#validate(org.diveintojee.poc.domain.AbstractEntity, org.diveintojee.poc.domain.validation.ValidationContext)}
     * .
     */
    @Test(expected = IllegalArgumentException.class)
    public final void validateShouldThrowIllegalArgumentExceptionWithNullContext() {

        // Variables
        AbstractEntity type;
        ValidationContext context;

        // Given
        type = Mockito.mock(Classified.class);
        context = null;

        // When
        underTest.validate(type, context);

    }

    /**
     * Test method for
     * {@link org.diveintojee.poc.business.impl.ValidatorImpl#validate(org.diveintojee.poc.domain.AbstractEntity, org.diveintojee.poc.domain.validation.ValidationContext)}
     * .
     */
    @Test(expected = IllegalArgumentException.class)
    public final void validateShouldThrowIllegalArgumentExceptionWithNullType() {

        // Variables
        AbstractEntity type;
        ValidationContext context;

        // Given
        type = null;
        context = ValidationContext.UPDATE;

        // When
        underTest.validate(type, context);

    }

}
