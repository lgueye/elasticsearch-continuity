package org.diveintojee.poc.persistence.store;

import org.diveintojee.poc.domain.AbstractEntity;
import org.diveintojee.poc.persistence.search.SearchEngine;
import org.hibernate.event.spi.PostInsertEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * User: lgueye Date: 24/10/12 Time: 19:28
 */
@RunWith(MockitoJUnitRunner.class)
public class PostInsertEventListenerTest {

    @Mock
    private SearchEngine searchEngine;

    @InjectMocks
    private PostInsertEventListener underTest;

    @Test
  public void onPostInsertShouldSucceed() throws Exception {
        PostInsertEvent event = mock(PostInsertEvent.class);
        underTest.onPostInsert(event);
        verify(searchEngine).index((AbstractEntity) event.getEntity());
        verifyNoMoreInteractions(searchEngine);
    }

}
