package org.diveintojee.poc.persistence.store;

import org.diveintojee.poc.domain.Classified;
import org.diveintojee.poc.domain.business.Validator;
import org.diveintojee.poc.domain.validation.ValidationContext;
import org.hibernate.event.spi.PreUpdateEvent;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

/**
 * User: louis.gueye@gmail.com Date: 24/10/12 Time: 19:30
 */
@RunWith(MockitoJUnitRunner.class)
public class PreUpdateEventListenerTest {

    @Mock
    private Validator validator;

    @InjectMocks
    private PreUpdateEventListener underTest;

    @Test
    public void onPreUpdateShouldSucceed() throws Exception {
        PreUpdateEvent event = mock(PreUpdateEvent.class);
        Classified eventEntity = mock(Classified.class);
        when(event.getEntity()).thenReturn(eventEntity);
        boolean result = underTest.onPreUpdate(event);
        verify(event).getEntity();
        verify(validator).validate(eventEntity, ValidationContext.UPDATE);
        verify(eventEntity).setUpdated(Matchers.<DateTime>any());
        assertFalse(result);
        verifyNoMoreInteractions(event, eventEntity, validator);
    }

}
