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
public class FullReIndexClassifiedsResourceTest {

    @Mock
    private Facade facade;

    @InjectMocks
    private FullReIndexClassifiedsResource underTest;

    @Test
    public void fullReIndexClassifiedsShouldSucceed() throws Throwable {

        // When
        underTest.fullReIndexClassifieds();

        // Then
        verify(facade).fullReIndexClassifieds();
        verifyNoMoreInteractions(facade);
    }
}
