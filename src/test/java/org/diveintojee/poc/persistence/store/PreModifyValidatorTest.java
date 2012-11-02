package org.diveintojee.poc.persistence.store;

import com.google.common.collect.Sets;

import org.diveintojee.poc.domain.Classified;
import org.diveintojee.poc.domain.validation.PreModifyValidator;
import org.diveintojee.poc.domain.validation.ValidationContext;
import org.hibernate.event.spi.AbstractPreDatabaseOperationEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * @author louis.gueye@gmail.com
 */
@RunWith(MockitoJUnitRunner.class)
public class PreModifyValidatorTest {
    @Mock
    private Validator validator;

    @InjectMocks
    private PreModifyValidator underTest;

    @Test(expected = IllegalArgumentException.class)
    public void validateShouldThrowIllegalArgumentExceptionWithNullEventEntity() throws Exception {
        Classified eventEntity = null;
        ValidationContext context = ValidationContext.DELETE;
        underTest.validate(eventEntity, context);
        verifyZeroInteractions(eventEntity, validator);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateShouldThrowIllegalArgumentExceptionWithNullValidationContext() throws Exception {
        AbstractPreDatabaseOperationEvent event = mock(AbstractPreDatabaseOperationEvent.class);
        Classified eventEntity = mock(Classified.class);
        when(event.getEntity()).thenReturn(eventEntity);
        ValidationContext context = null;
        underTest.validate(eventEntity, context);
        verify(event).getEntity();
        verifyNoMoreInteractions(event);
        verifyZeroInteractions(eventEntity, validator);
    }

    @Test(expected = ConstraintViolationException.class)
    public void validateShouldThrowConstraintViolationException() throws Exception {
        AbstractPreDatabaseOperationEvent event = mock(AbstractPreDatabaseOperationEvent.class);
        Classified eventEntity = mock(Classified.class);
        when(event.getEntity()).thenReturn(eventEntity);
        ValidationContext context = ValidationContext.CREATE;
        Class<?>[] groups = context.getContext();
        ConstraintViolation<Classified> constraintViolation = mock(ConstraintViolation.class);
        Set<ConstraintViolation<Classified>> violations = Sets.newHashSet(constraintViolation);
        when(validator.validate(eventEntity, groups)).thenReturn(violations);
        underTest.validate(eventEntity, context);
        verify(event).getEntity();
        verify(validator).validate(eventEntity, groups);
        verifyNoMoreInteractions(event, validator);
        verifyZeroInteractions(eventEntity);
    }

}
