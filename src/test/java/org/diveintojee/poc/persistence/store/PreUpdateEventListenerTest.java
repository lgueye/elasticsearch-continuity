package org.diveintojee.poc.persistence.store;

import org.diveintojee.poc.domain.Classified;
import org.diveintojee.poc.domain.validation.PreModifyValidator;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * User: lgueye Date: 24/10/12 Time: 19:30
 */
@RunWith(MockitoJUnitRunner.class)
public class PreUpdateEventListenerTest {

    @Mock
    private PreModifyValidator preModifyValidator;

    @InjectMocks
    private PreUpdateEventListener underTest;

    @Test
    public void onPreUpdateShouldSucceed() throws Exception {
        PreUpdateEvent event = mock(PreUpdateEvent.class);
        Classified eventEntity = mock(Classified.class);
        when(event.getEntity()).thenReturn(eventEntity);
        boolean result = underTest.onPreUpdate(event);
        verify(event).getEntity();
        verify(preModifyValidator).validate(eventEntity, ValidationContext.UPDATE);
        verify(eventEntity).setUpdated(Matchers.<DateTime>any());
        assertFalse(result);
        verifyNoMoreInteractions(event, eventEntity, preModifyValidator);
    }

}
