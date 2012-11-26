package org.diveintojee.poc.web;

import org.diveintojee.poc.domain.ResponseError;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: louis.gueye@gmail.com Date: 26/11/12 Time: 17:33
 */
@RunWith(MockitoJUnitRunner.class)
public class GenericExceptionMapperTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private ExceptionConverter exceptionConverter;

    @InjectMocks
    private GenericExceptionMapper underTest;

    @Test
    public void toResponseShouldUseDefaultMediaTypeWithUnsupportedInputMediaType() throws Exception {
      Throwable th = mock(Throwable.class);
      ResponseError error = mock(ResponseError.class);
      when(exceptionConverter.toResponseError(th, request)).thenReturn(error);
      when(request.getHeader("Accept")).thenReturn(MediaType.MULTIPART_FORM_DATA);

      Response response = underTest.toResponse(th);
      assertSame(error.getHttpStatus(), response.getStatus());
      assertSame(error, response.getEntity());
      assertSame(ExceptionConverter.DEFAULT_MEDIA_TYPE, response.getMetadata().getFirst("Content-Type"));
    }

    @Test
    public void toResponseShouldUseProvidedMediaType() throws Exception {

        Throwable th = mock(Throwable.class);
        ResponseError error = mock(ResponseError.class);
        when(exceptionConverter.toResponseError(th, request)).thenReturn(error);
        when(request.getHeader("Accept")).thenReturn(MediaType.APPLICATION_JSON);

        Response response = underTest.toResponse(th);
        assertSame(error.getHttpStatus(), response.getStatus());
        assertSame(error, response.getEntity());

    }
  
}
