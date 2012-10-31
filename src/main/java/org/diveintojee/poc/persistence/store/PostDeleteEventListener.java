/**
 *
 */
package org.diveintojee.poc.persistence.store;

import org.hibernate.event.spi.PostDeleteEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.midipascher.domain.AbstractEntity;
import fr.midipascher.persistence.search.SearchEngine;

/**
 * @author louis.gueye@gmail.com
 */
@Component(PostDeleteEventListener.BEAN_ID)
public class PostDeleteEventListener implements
        org.hibernate.event.spi.PostDeleteEventListener {

    public static final String BEAN_ID = "postDeleteEventListener";

    @Autowired
    private SearchEngine searchEngine;

    /**
     * @see org.hibernate.event.spi.PostDeleteEventListener#onPostDelete(org.hibernate.event.spi.PostDeleteEvent)
     * @param event
     */
    @Override
    public void onPostDelete(PostDeleteEvent event) {
        searchEngine.removeFromIndex( (AbstractEntity)event.getEntity());
    }
}
