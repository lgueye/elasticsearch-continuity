/*
 *
 */
package org.diveintojee.poc.domain.validation;

import com.google.common.collect.Sets;
import org.diveintojee.poc.domain.AbstractEntity;
import org.diveintojee.poc.domain.Classified;
import org.diveintojee.poc.domain.business.Validator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

/**
 * @author louis.gueye@gmail.com
 */
@RunWith(MockitoJUnitRunner.class)
public class ValidatorImplTest {

    @Mock
    private javax.validation.Validator validator;

    @InjectMocks
    private final Validator underTest = new ValidatorImpl();

    /**
     * Test method for
     * {@link org.diveintojee.poc.domain.validation.ValidatorImpl#validate(org.diveintojee.poc.domain.AbstractEntity, org.diveintojee.poc.domain.validation.ValidationContext)}
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

    @Test(expected = IllegalArgumentException.class)
    public void validateShouldThrowIllegalArgumentExceptionWithNullType() throws Exception {
        Classified classified = null;
        ValidationContext context = ValidationContext.DELETE;
        underTest.validate(classified, context);
        verifyZeroInteractions(classified, validator);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateShouldThrowIllegalArgumentExceptionWithNullContext() throws Exception {
        Classified classified = mock(Classified.class);
        ValidationContext context = null;
        underTest.validate(classified, context);
        verifyZeroInteractions(classified, validator);
    }

    @Test(expected = ConstraintViolationException.class)
    public void validateShouldThrowConstraintViolationWithNonEmptyViolations() throws Exception {
        Classified classified = mock(Classified.class);
        ValidationContext context = ValidationContext.CREATE;
        Class<?>[] groups = context.getContext();
        ConstraintViolation<Classified> constraintViolation = mock(ConstraintViolation.class);
        Set<ConstraintViolation<Classified>> violations = Sets.newHashSet(constraintViolation);
        when(validator.validate(classified, groups)).thenReturn(violations);
        underTest.validate(classified, context);
        verify(validator).validate(classified, groups);
        verifyNoMoreInteractions(validator);
        verifyZeroInteractions(classified);
    }

}
