package org.diveintojee.poc.web;

import org.diveintojee.poc.domain.Classified;
import org.diveintojee.poc.domain.business.Facade;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * User: louis.gueye@gmail.com Date: 26/11/12 Time: 18:31
 */
@RunWith(MockitoJUnitRunner.class)
public class ClassifiedsResourceTest {

    @Mock
    private Facade facade;

    @Mock
    private UriInfo uriInfo;

    @InjectMocks
    private ClassifiedsResource underTest;

    @Test
    public void createShouldSucceed() throws Throwable {
        Classified classified = mock(Classified.class);
        Long id = 4L;
        when(facade.createClassified(classified)).thenReturn(id);
        UriBuilder uriBuilder = mock(UriBuilder.class);
        when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
        when(uriBuilder.path(ClassifiedsResource.SINGLE_RESOURCE_PATH)).thenReturn(uriBuilder);
        URI uri = URI.create("http://foo");
        when(uriBuilder.build(String.valueOf(id))).thenReturn(uri);
        Response response = underTest.create(classified);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(uri, response.getMetadata().getFirst("Location"));
    }
  
    @Test
    public void deleteShouldSucceed() throws Throwable {

        Long id = 6L;
        Response response = underTest.delete(id);
        verify(facade).deleteClassified(id);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

    }

    @Test
    public void getShouldSucceed() throws Throwable {
        Long id = 6L;
        Classified classified = mock(Classified.class);
        when(facade.readClassified(id)).thenReturn(classified);
        Response response = underTest.get(id);
        verify(facade).readClassified(id);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertSame(classified, response.getEntity());
    }
  
}
