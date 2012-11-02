/*
 *
 */
package org.diveintojee.poc.web;

import org.apache.commons.lang3.StringUtils;
import org.diveintojee.poc.domain.Constants;
import org.diveintojee.poc.domain.ResponseError;
import org.diveintojee.poc.domain.exceptions.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

/**
 * @author louis.gueye@gmail.com
 */
@Component(ExceptionConverter.BEAN_ID)
public class ExceptionConverter {

    public static final String BEAN_ID = "ExceptionConverter";

    public static final List<String> SUPPORTED_MEDIA_TYPES = Arrays.asList(MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML);

    public static final String DEFAULT_MEDIA_TYPE = MediaType.APPLICATION_JSON;

    @Autowired
    private LocaleResolver localeResolver;

    @Autowired
    @Qualifier("messageSources")
    MessageSource messageSource;

    protected Locale getLocale(final HttpServletRequest request) {
        final Locale locale = this.localeResolver.resolveLocale(request);
        if (locale == null)
            return Constants.DEFAULT_LOCALE;
        if (!Constants.SUPPORTED_LOCALES.contains(locale.getLanguage()))
            return Constants.DEFAULT_LOCALE;
        return new Locale(locale.getLanguage());
    }

    /**
     * @param th
     * @return
     */
    public int resolveHttpStatus(final Throwable th) {
        if (th == null)
            return HttpServletResponse.SC_OK;
        // th.printStackTrace();

        if (th instanceof IllegalArgumentException || th instanceof ValidationException
                || th instanceof PersistenceException)
            return HttpServletResponse.SC_BAD_REQUEST;

        if (th instanceof BusinessException) {

            String messageCode = ((BusinessException) th).getMessageCode();

            if (StringUtils.isNotEmpty(messageCode) && messageCode.endsWith(".not.found"))
                return HttpServletResponse.SC_NOT_FOUND;

            return HttpServletResponse.SC_BAD_REQUEST;
        }

        if (th instanceof IllegalStateException)
            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

        if (th instanceof WebApplicationException && ((WebApplicationException) th).getResponse() != null)
            return ((WebApplicationException) th).getResponse().getStatus();

        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

    }

    /**
     * @param request
     * @param th
     * @return
     */
    public String resolveMesage(final HttpServletRequest request, final Throwable th) {
        if (th == null && request == null)
            return StringUtils.EMPTY;

        if (th == null)
            return StringUtils.EMPTY;

        if (request == null)
            return th.getMessage();

        if (th instanceof AuthenticationException)
            return this.messageSource.getMessage(String.valueOf(HttpServletResponse.SC_UNAUTHORIZED), null,
                    getLocale(request));

        if (th instanceof AccessDeniedException)
            return this.messageSource.getMessage(String.valueOf(HttpServletResponse.SC_FORBIDDEN), null,
                    getLocale(request));

        if (th instanceof ConstraintViolationException) {
            final ConstraintViolationException constraintViolationException = (ConstraintViolationException) th;
            final Set<ConstraintViolation<?>> violations = constraintViolationException.getConstraintViolations();
            final ConstraintViolation<?> violation = violations.iterator().next();
            return violation.getMessage();
        }

        if (th instanceof BusinessException) {
            BusinessException be = (BusinessException) th;
            return this.messageSource.getMessage(be.getMessageCode(), be.getMessageArgs(), getLocale(request));
        }

        return th.getMessage();
    }

    public ResponseError toResponseError(final Throwable th, final HttpServletRequest request) {
        final String message = resolveMesage(request, th);
        final int httpStatus = resolveHttpStatus(th);
        final ResponseError responseError = new ResponseError(message, httpStatus);
        return responseError;
    }
}
