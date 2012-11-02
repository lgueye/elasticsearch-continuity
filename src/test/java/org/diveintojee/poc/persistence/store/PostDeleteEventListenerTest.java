package org.diveintojee.poc.persistence.store;

import org.diveintojee.poc.domain.AbstractEntity;
import org.diveintojee.poc.persistence.search.SearchEngine;
import org.hibernate.event.spi.PostDeleteEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * User: lgueye Date: 24/10/12 Time: 19:27
 */
@RunWith(MockitoJUnitRunner.class)
public class PostDeleteEventListenerTest {

    @Mock
    private SearchEngine searchEngine;

    @InjectMocks
    private PostDeleteEventListener underTest;

  @Test
  public void onPostDeleteShouldSucceed() throws Exception {
      PostDeleteEvent event = mock(PostDeleteEvent.class);
      underTest.onPostDelete(event);
      verify(searchEngine).removeFromIndex((AbstractEntity) event.getEntity());
      verifyNoMoreInteractions(searchEngine);
  }
}
