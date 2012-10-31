/**
 *
 */
package org.diveintojee.poc.persistence.store;

import org.hibernate.event.spi.PreInsertEvent;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.midipascher.domain.AbstractEntity;
import fr.midipascher.domain.EventAware;
import fr.midipascher.domain.LocationAware;
import fr.midipascher.domain.validation.ValidationContext;

/**
 * @author louis.gueye@gmail.com
 */
@Component(PreInsertEventListener.BEAN_ID)
public class PreInsertEventListener implements
        org.hibernate.event.spi.PreInsertEventListener {

    public static final String BEAN_ID = "preInsertEventListener";

    @Autowired
    private PreModifyValidator preModifyValidator;

    /**
     *
     */
    private static final long serialVersionUID = 2153376355687873385L;

    @Autowired
    private Geocoder geocoder;

    /**
     * @param event
     * @return
     * @see org.hibernate.event.spi.PreInsertEventListener#onPreInsert(org.hibernate.event.spi.PreInsertEvent)
     */
    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        final Object eventEntity = event.getEntity();
        preModifyValidator.validate((AbstractEntity) eventEntity, ValidationContext.CREATE);
        if (eventEntity instanceof EventAware) {
            ((EventAware) eventEntity).setCreated(new DateTime());
        }
        if (eventEntity instanceof LocationAware) {
            geocoder.latLong(((LocationAware) eventEntity).getAddress());
        }
        return false;
    }

}
