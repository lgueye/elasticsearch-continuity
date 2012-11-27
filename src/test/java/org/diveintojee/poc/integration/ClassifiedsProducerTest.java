package org.diveintojee.poc.integration;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author louis.gueye@gmail.com
 */
public class ClassifiedsProducerTest {

    private ClassifiedsProducer underTest;

    @Before
    public void before() {
        underTest = new ClassifiedsProducer();
    }

    @Test
    public void writeShouldNotifyListeners() {
        WriteClassifiedEventListener listener = mock(WriteClassifiedEventListener.class);
        underTest.registerListener(listener);
        WriteClassifiedCommand command = mock(WriteClassifiedCommand.class);
        underTest.write(command);
        verify(listener).onMessage();
    }

}
