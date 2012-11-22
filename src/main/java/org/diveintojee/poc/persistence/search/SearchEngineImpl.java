package org.diveintojee.poc.persistence.search;

import org.diveintojee.poc.domain.AbstractEntity;
import org.diveintojee.poc.domain.Classified;
import org.diveintojee.poc.persistence.store.BaseDao;
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
    private BaseDao baseDao;

    @Autowired
    private ClassifiedCriteriaToQueryBuilderConverter classifiedCriteriaToQueryBuilderConverter;

    @Autowired
    private SearchResponseToClassifiedsListConverter searchResponseToClassifiedsListConverter;

    @Autowired
    private ClassifiedToJsonByteArrayConverter classifiedToByteArrayConverter;

    @Override
    public List<Classified> findClassifiedsByCriteria(Classified criteria) {
        QueryBuilder queryBuilder = classifiedCriteriaToQueryBuilderConverter.convert(criteria);
        SearchResponse searchResponse = elasticsearch
            .prepareSearch(INDEX_NAME)
            .setTypes(CLASSIFIED_TYPE_NAME)
            .setQuery(queryBuilder)
            .addSort(SortBuilders.fieldSort("created").order(SortOrder.DESC))
            .addSort(SortBuilders.fieldSort("updated").order(SortOrder.DESC))
            .execute().actionGet();
        return searchResponseToClassifiedsListConverter.convert(searchResponse);
    }

    @Override
    public void index(AbstractEntity entity) {
        if (entity instanceof Classified) {
            byte[] accountAsBytes = classifiedToByteArrayConverter.convert((Classified) entity);
            elasticsearch.prepareIndex(INDEX_NAME, CLASSIFIED_TYPE_NAME).setId(entity.getId().toString()).setSource(accountAsBytes).setRefresh(true).execute().actionGet();
        }
    }

    @Override
    public void removeFromIndex(AbstractEntity entity) {
        if (entity instanceof Classified) {
            elasticsearch.prepareDelete(INDEX_NAME, CLASSIFIED_TYPE_NAME, entity.getId().toString()).setRefresh(true).execute().actionGet();
        }
    }

    @Override
    public void reindexClassifieds() {
        List<Classified> classifieds = baseDao.findAll(Classified.class);
      final
      boolean
          acknowledged =
          elasticsearch.admin().indices().prepareCreate("writeclassifieds").execute().actionGet()
              .acknowledged();
      if (!acknowledged) throw new IllegalStateException("Ack expected");

    }
}
