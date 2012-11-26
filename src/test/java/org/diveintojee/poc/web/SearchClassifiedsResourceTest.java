package org.diveintojee.poc.web;

import com.google.common.collect.Lists;

import org.diveintojee.poc.domain.Classified;
import org.diveintojee.poc.domain.business.Facade;
import org.hibernate.Criteria;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * User: louis.gueye@gmail.com Date: 26/11/12 Time: 17:04
 */
@RunWith(MockitoJUnitRunner.class)
public class SearchClassifiedsResourceTest {

    @Mock
    private Facade facade;

    @InjectMocks
    private SearchClassifiedsResource underTest;

    @Test
    public void findShouldSucceed() throws Throwable {

        List<Classified> classifieds = Lists.newArrayList(mock(Classified.class));
        Classified criteria = mock(Classified.class);
        when(facade.findClassifiedsByCriteria(criteria)).thenReturn(classifieds);

        // When
        Response result = underTest.find(criteria);

        // Then
        verify(facade).findClassifiedsByCriteria(criteria);
        assertSame(classifieds, ((GenericEntity)result.getEntity()).getEntity());
        verifyNoMoreInteractions(facade);
      
    }
}
