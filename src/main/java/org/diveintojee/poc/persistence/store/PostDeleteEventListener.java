/**
 *
 */
package org.diveintojee.poc.persistence.store;

import org.diveintojee.poc.domain.Classified;
import org.diveintojee.poc.integration.ClassifiedsProducer;
import org.diveintojee.poc.integration.Operation;
import org.diveintojee.poc.integration.WriteClassifiedCommand;
import org.hibernate.event.spi.PostDeleteEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author louis.gueye@gmail.com
 */
@Component(PostDeleteEventListener.BEAN_ID)
public class PostDeleteEventListener implements
        org.hibernate.event.spi.PostDeleteEventListener {

    public static final String BEAN_ID = "postDeleteEventListener";

    @Autowired
    private ClassifiedsProducer classifiedsProducer;

    /**
     * @param event
     * @see org.hibernate.event.spi.PostDeleteEventListener#onPostDelete(org.hibernate.event.spi.PostDeleteEvent)
     */
    @Override
    public void onPostDelete(PostDeleteEvent event) {
        classifiedsProducer.write(
                new WriteClassifiedCommand((Classified) event.getEntity(), Operation.delete));
    }
}
