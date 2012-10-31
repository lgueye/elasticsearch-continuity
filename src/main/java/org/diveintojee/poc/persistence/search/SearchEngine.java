package org.diveintojee.poc.persistence.search;

import java.util.List;

import fr.midipascher.domain.AbstractEntity;
import fr.midipascher.domain.Restaurant;

/**
 * @author louis.gueye@gmail.com
 */
public interface SearchEngine {

    String INDEX_NAME = SearchIndices.midipascher.toString();

    String RESTAURANT_TYPE_NAME = SearchTypes.restaurant.toString();

    List<Restaurant> findRestaurantsByCriteria(Restaurant criteria);

    void index(AbstractEntity entity);

    void removeFromIndex(AbstractEntity entity);
}
