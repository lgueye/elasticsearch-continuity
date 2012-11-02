package org.diveintojee.poc.persistence.store;

import org.hibernate.event.spi.PreDeleteEvent;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

/**
 * User: lgueye Date: 24/10/12 Time: 19:29
 */
public class PreDeleteEventListenerTest {

    private PreDeleteEventListener underTest;

    @Before
    public void before() {
        underTest = new PreDeleteEventListener();
    }

    @Test
    public void onPreDeleteShouldSucceed() throws Exception {
        PreDeleteEvent event = mock(PreDeleteEvent.class);
        boolean result = underTest.onPreDelete(event);
        assertFalse(result);
    }

}
