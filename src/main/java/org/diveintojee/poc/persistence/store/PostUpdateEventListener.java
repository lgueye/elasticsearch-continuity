/**
 *
 */
package org.diveintojee.poc.persistence.store;

import org.hibernate.event.spi.PostUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.midipascher.domain.AbstractEntity;
import fr.midipascher.persistence.search.SearchEngine;

/**
 * @author louis.gueye@gmail.com
 */
@Component(PostUpdateEventListener.BEAN_ID)
public class PostUpdateEventListener implements
        org.hibernate.event.spi.PostUpdateEventListener {

    public static final String BEAN_ID = "postUpdateEventListener";

    @Autowired
    private SearchEngine searchEngine;

    /**
     * @see org.hibernate.event.spi.PostUpdateEventListener#onPostUpdate(org.hibernate.event.spi.PostUpdateEvent)
     *
     * @param event
     */
    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        searchEngine.index( (AbstractEntity)event.getEntity());
    }
}
