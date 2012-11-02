package org.diveintojee.poc.persistence.store;

import org.diveintojee.poc.domain.Classified;
import org.diveintojee.poc.domain.validation.PreModifyValidator;
import org.diveintojee.poc.domain.validation.ValidationContext;
import org.hibernate.event.spi.PreInsertEvent;
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
 * louis.gueye@gmail.com
 */
@RunWith(MockitoJUnitRunner.class)
public class PreInsertEventListenerTest {

    @Mock
    private PreModifyValidator preModifyValidator;

    @InjectMocks
    private PreInsertEventListener underTest;

    @Test
    public void onPreInsertShouldSucceed() throws Exception {
        PreInsertEvent event = mock(PreInsertEvent.class);
        Classified eventEntity = mock(Classified.class);
        when(event.getEntity()).thenReturn(eventEntity);
        boolean result = underTest.onPreInsert(event);
        verify(event).getEntity();
        verify(preModifyValidator).validate(eventEntity, ValidationContext.CREATE);
        verify(eventEntity).setCreated(Matchers.<DateTime>any());
        assertFalse(result);
        verifyNoMoreInteractions(event, eventEntity, preModifyValidator);
    }
}
