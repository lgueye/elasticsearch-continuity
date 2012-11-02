/*
 *
 */
package org.diveintojee.poc.business;

import org.diveintojee.poc.TestConstants;
import org.diveintojee.poc.TestFixtures;
import org.diveintojee.poc.domain.AbstractEntity;
import org.diveintojee.poc.domain.business.Validator;
import org.diveintojee.poc.domain.validation.ValidationContext;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

import javax.validation.ConstraintViolationException;

import static org.junit.Assert.fail;

/**
 * Facade integration testing<br/>
 *
 * @author louis.gueye@gmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {TestConstants.SERVER_CONTEXT,
        TestConstants.VALIDATION_CONTEXT, TestConstants.SEARCH_CONTEXT_TEST})
public abstract class BaseValidations {

    @Autowired
    private Validator validator;

    public void assertExpectedViolation(final AbstractEntity type, final ValidationContext context,
                                        final Locale locale, final String expectedMessage, final String expectedPath) {
        try {
            LocaleContextHolder.setLocale(locale);
            validator.validate(type, context);
            fail("Expected " + ConstraintViolationException.class.getName());
        } catch (final ConstraintViolationException constraintViolationException) {
            TestFixtures.assertViolationContainsTemplateAndMessage(constraintViolationException,
                                                                   expectedMessage,
                                                                   expectedPath);
        } catch (final Throwable throwable) {
            fail("Expected " + ConstraintViolationException.class.getName() + ", got " + throwable.getClass().getName());
            throwable.printStackTrace();
        }
    }
}
