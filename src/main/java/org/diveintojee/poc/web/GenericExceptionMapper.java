/**
 *
 */
package org.diveintojee.poc.web;

import org.apache.commons.lang3.StringUtils;
import org.diveintojee.poc.domain.ResponseError;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author louis.gueye@gmail.com
 */
@Component
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Context
    private HttpServletRequest request;

    @Autowired
    private ExceptionConverter exceptionConverter;

    /**
     * @see javax.ws.rs.ext.ExceptionMapper#toResponse(Throwable)
     */
    @Override
    public Response toResponse(final Throwable th) {

        final ResponseError error = this.exceptionConverter.toResponseError(th, this.request);

        final String preferredResponseMediaType = this.request.getHeader("Accept");

        final boolean shouldUseDefaultMediaType = StringUtils.isNotEmpty(preferredResponseMediaType)
                          && !ExceptionConverter.SUPPORTED_MEDIA_TYPES
            .contains(preferredResponseMediaType);

        if (shouldUseDefaultMediaType) {
            LoggerFactory.getLogger(GenericExceptionMapper.class).debug(
                      "Preferred Media type {} not supported, using default {}", preferredResponseMediaType,
                      ExceptionConverter.DEFAULT_MEDIA_TYPE);
            return Response.status(error.getHttpStatus()).header("Content-Type", ExceptionConverter.DEFAULT_MEDIA_TYPE)
                      .entity(error).build();
        }

        return Response.status(error.getHttpStatus()).entity(error).build();
    }

}
