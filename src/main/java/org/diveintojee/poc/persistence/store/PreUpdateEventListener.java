/**
 *
 */
package org.diveintojee.poc.persistence.store;

import org.diveintojee.poc.domain.AbstractEntity;
import org.diveintojee.poc.domain.Classified;
import org.diveintojee.poc.domain.business.Validator;
import org.diveintojee.poc.domain.validation.ValidationContext;
import org.hibernate.event.spi.PreUpdateEvent;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author louis.gueye@gmail.com
 */
@Component(PreUpdateEventListener.BEAN_ID)
public class PreUpdateEventListener implements
        org.hibernate.event.spi.PreUpdateEventListener {

    @Autowired
    private Validator validator;

    public static final String BEAN_ID = "preUpdateEventListener";

    /**
     *
     */
    private static final long serialVersionUID = 2153376355687873385L;

    /**
     * @see org.hibernate.event.spi.PreUpdateEventListener#onPreUpdate(org.hibernate.event.spi.PreUpdateEvent)
     */
    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        final Object eventEntity = event.getEntity();
        validator.validate((AbstractEntity) eventEntity, ValidationContext.UPDATE);
        if (eventEntity instanceof Classified) {
            ((Classified) eventEntity).setUpdated(new DateTime());
        }
        return false;
    }

}
