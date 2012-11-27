package org.diveintojee.poc.integration;

import org.diveintojee.poc.domain.Classified;
import org.diveintojee.poc.persistence.search.SearchEngine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

/**
 * @author louis.gueye@gmail.com
 */
@RunWith(MockitoJUnitRunner.class)
public class ClassifiedsConsumerTest {

    @Mock
    private ClassifiedsProducer classifiedsProducer;

    @Mock
    private SearchEngine searchEngine;

    @InjectMocks
    private ClassifiedsConsumer underTest;

    @Test
    public void stopConsumingWriteCommandsShouldUnregisterToProducer() throws Exception {
        underTest.stopConsumingWriteCommands();
        verify(classifiedsProducer).unregisterListener(underTest);
        verifyNoMoreInteractions(classifiedsProducer);
    }

    @Test
    public void startConsumingWriteCommandsShouldRegisterToProducer() throws Exception {
        underTest.startConsumingWriteCommands();
        verify(classifiedsProducer).registerListener(underTest);
    }

    @Test
    public void onMessageShouldNotIndexWithEmptyQueue() throws Exception {
        WriteClassifiedCommand command = null;
        when(classifiedsProducer.consume()).thenReturn(command);
        underTest.onMessage();
        verifyZeroInteractions(searchEngine);
    }

    @Test
    public void onMessageShouldRemoveFromIndex() throws Exception {
        WriteClassifiedCommand command = mock(WriteClassifiedCommand.class);
        when(classifiedsProducer.consume()).thenReturn(command);
        when(command.getOperation()).thenReturn(Operation.delete);
        Classified classified = mock(Classified.class);
        when(command.getClassified()).thenReturn(classified);
        underTest.onMessage();
        verify(searchEngine).removeFromIndex(classified);
    }

    @Test
    public void onMessageShouldWriteToIndex() throws Exception {
        WriteClassifiedCommand command = mock(WriteClassifiedCommand.class);
        when(classifiedsProducer.consume()).thenReturn(command);
        when(command.getOperation()).thenReturn(Operation.write);
        Classified classified = mock(Classified.class);
        when(command.getClassified()).thenReturn(classified);
        underTest.onMessage();
        verify(searchEngine).index(classified);
    }
}
