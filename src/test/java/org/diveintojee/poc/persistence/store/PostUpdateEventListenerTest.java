package org.diveintojee.poc.persistence.store;

import org.diveintojee.poc.integration.ClassifiedsProducer;
import org.diveintojee.poc.integration.Operation;
import org.diveintojee.poc.integration.WriteClassifiedCommand;
import org.hibernate.event.spi.PostUpdateEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * User: lgueye Date: 24/10/12 Time: 19:28
 */
@RunWith(MockitoJUnitRunner.class)
public class PostUpdateEventListenerTest {

    @Mock
    private ClassifiedsProducer classifiedsProducer;

    @InjectMocks
    private PostUpdateEventListener underTest;

    @Test
    public void onPostUpdateShouldSucceed() throws Exception {
        PostUpdateEvent event = mock(PostUpdateEvent.class);
        underTest.onPostUpdate(event);
        final ArgumentCaptor<WriteClassifiedCommand> argumentCaptor =
                ArgumentCaptor.forClass(WriteClassifiedCommand.class);
        verify(classifiedsProducer).write(argumentCaptor.capture());
        assertEquals(event.getEntity(), argumentCaptor.getValue().getClassified());
        assertEquals(Operation.write, argumentCaptor.getValue().getOperation());
        verifyNoMoreInteractions(classifiedsProducer);
    }

}

