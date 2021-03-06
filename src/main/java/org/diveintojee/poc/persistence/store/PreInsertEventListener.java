/**
 *
 */
package org.diveintojee.poc.persistence.store;

import org.diveintojee.poc.domain.AbstractEntity;
import org.diveintojee.poc.domain.Classified;
import org.diveintojee.poc.domain.business.Validator;
import org.diveintojee.poc.domain.validation.ValidationContext;
import org.hibernate.event.spi.PreInsertEvent;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author louis.gueye@gmail.com
 */
@Component(PreInsertEventListener.BEAN_ID)
public class PreInsertEventListener implements
        org.hibernate.event.spi.PreInsertEventListener {

    public static final String BEAN_ID = "preInsertEventListener";

    @Autowired
    private Validator validator;

    /**
     *
     */
    private static final long serialVersionUID = 2153376355687873385L;

    /**
     * @param event
     * @return
     * @see org.hibernate.event.spi.PreInsertEventListener#onPreInsert(org.hibernate.event.spi.PreInsertEvent)
     */
    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        final Object eventEntity = event.getEntity();
        validator.validate((AbstractEntity) eventEntity, ValidationContext.CREATE);
        if (eventEntity instanceof Classified) {
            ((Classified) eventEntity).setCreated(new DateTime());
        }
        return false;
    }

}
