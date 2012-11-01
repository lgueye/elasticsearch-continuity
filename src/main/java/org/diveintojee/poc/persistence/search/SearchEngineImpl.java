package org.diveintojee.poc.persistence.search;

import org.diveintojee.poc.domain.AbstractEntity;
import org.diveintojee.poc.domain.Account;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author louis.gueye@gmail.com
 */
@Repository
public class SearchEngineImpl implements SearchEngine {

    @Autowired
    private Client elasticsearch;

    @Autowired
    private AccountToQueryBuilderConverter accountToQueryBuilderConverter;

    @Autowired
    private SearchResponseToAccountsListConverter searchResponseToAccountsListConverter;

    @Autowired
    private AccountToJsonByteArrayConverter accountToByteArrayConverter;

    @Override
    public List<Account> findAccountsByCriteria(Account criteria) {
        QueryBuilder queryBuilder = accountToQueryBuilderConverter.convert(criteria);
        SearchResponse searchResponse = elasticsearch
            .prepareSearch(INDEX_NAME)
            .setTypes(RESTAURANT_TYPE_NAME)
            .setQuery(queryBuilder)
            .addSort(SortBuilders.fieldSort("created").order(SortOrder.DESC))
            .addSort(SortBuilders.fieldSort("updated").order(SortOrder.DESC))
            .execute().actionGet();
        return searchResponseToAccountsListConverter.convert(searchResponse);
    }

    @Override
    public void index(AbstractEntity entity) {
        if (entity instanceof Account) {
            byte[] accountAsBytes = accountToByteArrayConverter.convert((Account) entity);
            elasticsearch.prepareIndex(INDEX_NAME, RESTAURANT_TYPE_NAME).setId(entity.getId().toString()).setSource(accountAsBytes).setRefresh(true).execute().actionGet();
        }
    }

    @Override
    public void removeFromIndex(AbstractEntity entity) {
        if (entity instanceof Account) {
            elasticsearch.prepareDelete(INDEX_NAME, RESTAURANT_TYPE_NAME, entity.getId().toString()).setRefresh(true).execute().actionGet();
        }
    }
}
