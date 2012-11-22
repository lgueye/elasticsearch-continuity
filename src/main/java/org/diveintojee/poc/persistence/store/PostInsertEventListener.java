/**
 *
 */
package org.diveintojee.poc.persistence.store;

import org.diveintojee.poc.domain.Classified;
import org.diveintojee.poc.integration.ClassifiedsProducer;
import org.diveintojee.poc.integration.Operation;
import org.diveintojee.poc.integration.WriteClassifiedCommand;
import org.hibernate.event.spi.PostInsertEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author louis.gueye@gmail.com
 */
@Component(PostInsertEventListener.BEAN_ID)
public class PostInsertEventListener implements
        org.hibernate.event.spi.PostInsertEventListener {

    public static final String BEAN_ID = "postInsertEventListener";

    @Autowired
    private ClassifiedsProducer classifiedsProducer;

    /**
     * @param event
     * @see org.hibernate.event.spi.PostInsertEventListener#onPostInsert(org.hibernate.event.spi.PostInsertEvent)
     */
    @Override
    public void onPostInsert(PostInsertEvent event) {
        classifiedsProducer.write(new WriteClassifiedCommand((Classified) event.getEntity(), Operation.write));
    }
}
