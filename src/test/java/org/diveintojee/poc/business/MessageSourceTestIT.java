/*
 *
 */
package org.diveintojee.poc.business;

import org.diveintojee.poc.TestConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 * Authority database integration testing<br/>
 * CRUD operations are tested<br>
 * Finders are tested<br/>
 *
 * @author louis.gueye@gmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {TestConstants.VALIDATION_CONTEXT})
public class MessageSourceTestIT {

    @Autowired
    @Qualifier("messageSources")
    private MessageSource underTest;

    @Test
    public final void messageSourceShouldConsiderEncoding() {

        assertEquals("L'identifiant est requis",
                this.underTest.getMessage("classified.id.required", null, Locale.FRENCH));

    }

}
