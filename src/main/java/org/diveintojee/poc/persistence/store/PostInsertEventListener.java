/**
 *
 */
package org.diveintojee.poc.persistence.store;

import org.diveintojee.poc.domain.AbstractEntity;
import org.diveintojee.poc.persistence.search.SearchEngine;
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
    private SearchEngine searchEngine;

    /**
     * @see org.hibernate.event.spi.PostInsertEventListener#onPostInsert(org.hibernate.event.spi.PostInsertEvent)
     * @param event
     */
    @Override
    public void onPostInsert(PostInsertEvent event) {
        searchEngine.index( (AbstractEntity)event.getEntity());
    }
}
