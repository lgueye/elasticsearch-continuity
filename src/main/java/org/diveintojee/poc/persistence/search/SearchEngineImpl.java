package org.diveintojee.poc.persistence.search;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import fr.midipascher.domain.AbstractEntity;
import fr.midipascher.domain.Restaurant;

/**
 * @author louis.gueye@gmail.com
 */
@Repository
public class SearchEngineImpl implements SearchEngine {

    @Autowired
    private Client elasticsearch;

    @Autowired
    private RestaurantToQueryBuilderConverter restaurantToQueryBuilderConverter;

    @Autowired
    private SearchResponseToRestaurantsListConverter searchResponseToRestaurantsListConverter;

    @Autowired
    private RestaurantToJsonByteArrayConverter restaurantToByteArrayConverter;

    @Override
    public List<Restaurant> findRestaurantsByCriteria(Restaurant criteria) {
        QueryBuilder queryBuilder = restaurantToQueryBuilderConverter.convert(criteria);
        SearchResponse searchResponse = elasticsearch
            .prepareSearch(INDEX_NAME)
            .setTypes(RESTAURANT_TYPE_NAME)
            .setQuery(queryBuilder)
            .addSort(SortBuilders.fieldSort("created").order(SortOrder.DESC))
            .addSort(SortBuilders.fieldSort("updated").order(SortOrder.DESC))
            .execute().actionGet();
        return searchResponseToRestaurantsListConverter.convert(searchResponse);
    }

    @Override
    public void index(AbstractEntity entity) {
        if (entity instanceof Restaurant) {
            byte[] restaurantAsBytes = restaurantToByteArrayConverter.convert((Restaurant) entity);
            elasticsearch.prepareIndex(INDEX_NAME, RESTAURANT_TYPE_NAME).setId(entity.getId().toString()).setSource(restaurantAsBytes).setRefresh(true).execute().actionGet();
        }
    }

    @Override
    public void removeFromIndex(AbstractEntity entity) {
        if (entity instanceof Restaurant) {
            elasticsearch.prepareDelete(INDEX_NAME, RESTAURANT_TYPE_NAME, entity.getId().toString()).setRefresh(true).execute().actionGet();
        }
    }
}
