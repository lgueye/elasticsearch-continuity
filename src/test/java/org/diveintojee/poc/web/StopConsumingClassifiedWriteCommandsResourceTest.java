package org.diveintojee.poc.web;

import org.diveintojee.poc.domain.business.Facade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * User: louis.gueye@gmail.com Date: 26/11/12 Time: 17:04
 */
@RunWith(MockitoJUnitRunner.class)
public class StopConsumingClassifiedWriteCommandsResourceTest {

    @Mock
    private Facade facade;

    @InjectMocks
    private StopConsumingClassifiedWriteCommandsResource underTest;

    @Test
    public void stopConsumingShouldSucceed() throws Throwable {

        // When
        underTest.stopConsuming();

        // Then
        verify(facade).stopConsumingClassifiedsWriteCommands();
        verifyNoMoreInteractions(facade);
    }
}
