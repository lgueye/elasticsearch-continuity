package org.diveintojee.poc.persistence.store;

import org.diveintojee.poc.integration.ClassifiedsProducer;
import org.diveintojee.poc.integration.Operation;
import org.diveintojee.poc.integration.WriteClassifiedCommand;
import org.hibernate.event.spi.PostDeleteEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * User: louis.gueye@gmail.com Date: 24/10/12 Time: 19:27
 */
@RunWith(MockitoJUnitRunner.class)
public class PostDeleteEventListenerTest {

    @Mock
    private ClassifiedsProducer classifiedsProducer;

    @InjectMocks
    private PostDeleteEventListener underTest;

    @Test
    public void onPostDeleteShouldSucceed() throws Exception {
        PostDeleteEvent event = mock(PostDeleteEvent.class);
        underTest.onPostDelete(event);
        final ArgumentCaptor<WriteClassifiedCommand> argumentCaptor =
                ArgumentCaptor.forClass(WriteClassifiedCommand.class);
        verify(classifiedsProducer).write(argumentCaptor.capture());
        assertEquals(event.getEntity(), argumentCaptor.getValue().getClassified());
        assertEquals(Operation.delete, argumentCaptor.getValue().getOperation());
        verifyNoMoreInteractions(classifiedsProducer);
    }
}
