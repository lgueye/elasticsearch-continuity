/**
 *
 */
package org.diveintojee.poc.web;

import org.apache.commons.lang3.StringUtils;
import org.diveintojee.poc.domain.exceptions.BusinessException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

/**
 * @author louis.gueye@gmail.com
 */
@RunWith(MockitoJUnitRunner.class)
public class ExceptionConverterTest {

    @Mock
    MessageSource messageSource;

    @Mock
    LocaleResolver localeResolver;

    @InjectMocks
    private final ExceptionConverter underTest = new ExceptionConverter();

    /**
     * Test method for {@link org.diveintojee.poc.web.ExceptionConverter#resolveHttpStatus(Throwable)} .
     */
    @Test
    public final void resolveHttpStatusShouldMapTo400WithIllegalArgumentException() {
        Throwable th = new IllegalArgumentException();
        int httpStatus = this.underTest.resolveHttpStatus(th);
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), httpStatus);
    }

    /**
     * Test method for {@link org.diveintojee.poc.web.ExceptionConverter#resolveHttpStatus(Throwable)} .
     */
    @Test
    public final void resolveHttpStatusShouldMapTo500WithIllegalStateException() {
        Throwable th = new IllegalStateException();
        int httpStatus = this.underTest.resolveHttpStatus(th);
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), httpStatus);
    }

    /**
     * Test method for {@link org.diveintojee.poc.web.ExceptionConverter#resolveHttpStatus(Throwable)} .
     */
    @Test
    public final void resolveHttpStatusShouldMapTo404WithBusinessExceptionAndNotFoundMessageCode() {
        Throwable th = new BusinessException("anything.not.found", null, "default message");
        int httpStatus = this.underTest.resolveHttpStatus(th);
        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), httpStatus);
    }

    /**
     * Test method for {@link org.diveintojee.poc.web.ExceptionConverter#resolveHttpStatus(Throwable)} .
     */
    @Test
    public final void resolveHttpStatusShouldMapTo200WithNullException() {
        Throwable th = null;
        int httpStatus = this.underTest.resolveHttpStatus(th);
        Assert.assertEquals(HttpStatus.OK.value(), httpStatus);
    }

    /**
     * Test method for {@link org.diveintojee.poc.web.ExceptionConverter#resolveHttpStatus(Throwable)} .
     */
    @Test
    public final void resolveMessageShouldReturnEmptyWithBothNullExceptionAndNullRequest() {
        Throwable th = null;
        HttpServletRequest request = null;
        String message = this.underTest.resolveMesage(request, th);
        Assert.assertEquals(StringUtils.EMPTY, message);
    }

    /**
     * Test method for {@link org.diveintojee.poc.web.ExceptionConverter#resolveHttpStatus(Throwable)} .
     */
    @Test
    public final void resolveMessageShouldReturnEmptyWithNullException() {
        Throwable th = null;
        HttpServletRequest request = new MockHttpServletRequest();
        String message = this.underTest.resolveMesage(request, th);
        Assert.assertEquals(StringUtils.EMPTY, message);
    }

    /**
     * Test method for {@link org.diveintojee.poc.web.ExceptionConverter#resolveHttpStatus(Throwable)} .
     */
    @Test
    public final void resolveMessageShouldReturnUnlocalizedMessageWithNullRequest() {
        String nonLocalizedMessage = "message en francais";
        Throwable th = new IllegalArgumentException(nonLocalizedMessage);
        HttpServletRequest request = null;
        String message = this.underTest.resolveMesage(request, th);
        Assert.assertEquals(nonLocalizedMessage, message);
    }

    /**
     * Test method for {@link org.diveintojee.poc.web.ExceptionConverter#resolveHttpStatus(Throwable)} .
     */
    @Test
    public final void resolveMessageShouldReturnUnlocalizedMessageWithNonLocalizedException() {
        String nonLocalizedMessage = "message en francais";
        Throwable th = new IllegalArgumentException(nonLocalizedMessage);
        HttpServletRequest request = new MockHttpServletRequest();
        String message = this.underTest.resolveMesage(request, th);
        Assert.assertEquals(nonLocalizedMessage, message);
    }

    /**
     * Test method for {@link org.diveintojee.poc.web.ExceptionConverter#resolveHttpStatus(Throwable)} .
     */
    @Test
    public final void resolveMessageShouldInvokeLocalizedExceptionMessageResolver() {
        String code = "message.code";
        Object[] args = new Object[]{};
        Locale locale = Locale.FRENCH;

        BusinessException th = Mockito.mock(BusinessException.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(th.getMessageCode()).thenReturn(code);
        Mockito.when(th.getMessageArgs()).thenReturn(args);
        Mockito.when(this.localeResolver.resolveLocale(request)).thenReturn(locale);
        this.underTest.resolveMesage(request, th);
        Mockito.verify(th).getMessageCode();
        Mockito.verify(th).getMessageArgs();
        Mockito.verify(this.messageSource).getMessage(code, args, locale);
        Mockito.verify(this.localeResolver).resolveLocale(request);
    }

}
