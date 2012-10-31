/**
 *
 */
package org.diveintojee.poc.persistence.store;

import org.hibernate.event.spi.PreUpdateEvent;
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
@Component(PreUpdateEventListener.BEAN_ID)
public class PreUpdateEventListener implements
        org.hibernate.event.spi.PreUpdateEventListener {

    @Autowired
    private PreModifyValidator preModifyValidator;

    public static final String BEAN_ID = "preUpdateEventListener";

    @Autowired
    private Geocoder geocoder;

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
        preModifyValidator.validate((AbstractEntity) eventEntity, ValidationContext.UPDATE);
        if (eventEntity instanceof EventAware) {
          ((EventAware)eventEntity).setUpdated(new DateTime());
        }
        if (eventEntity instanceof LocationAware) {
            geocoder.latLong(((LocationAware) eventEntity).getAddress());
        }
        return false;
    }

}
