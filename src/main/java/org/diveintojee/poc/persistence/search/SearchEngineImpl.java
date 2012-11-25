package org.diveintojee.poc.persistence.search;

import org.diveintojee.poc.domain.AbstractEntity;
import org.diveintojee.poc.domain.Classified;
import org.diveintojee.poc.persistence.search.factory.DropCreateIndexCommand;
import org.diveintojee.poc.persistence.search.factory.ElasticSearchConfigResolver;
import org.diveintojee.poc.persistence.store.BaseDao;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author louis.gueye@gmail.com
 */
@Repository
public class SearchEngineImpl implements SearchEngine {

    @Autowired
    private Client elasticSearch;

    @Autowired
    private BaseDao baseDao;

    @Autowired
    private ClassifiedCriteriaToQueryBuilderConverter classifiedCriteriaToQueryBuilderConverter;

    @Autowired
    private SearchResponseToClassifiedsListConverter searchResponseToClassifiedsListConverter;

    @Autowired
    private ClassifiedToJsonByteArrayConverter classifiedToByteArrayConverter;

    @Autowired
    private ElasticSearchConfigResolver elasticSearchConfigResolver;

    @Autowired
    private DropCreateIndexCommand dropCreateIndexCommand;


    @Override
    public List<Classified> findClassifiedsByCriteria(Classified criteria) {
        QueryBuilder queryBuilder = classifiedCriteriaToQueryBuilderConverter.convert(criteria);
        SearchResponse searchResponse = elasticSearch
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
            byte[] classifiedAsBytes = classifiedToByteArrayConverter.convert((Classified) entity);
            elasticSearch.prepareIndex(INDEX_NAME, CLASSIFIED_TYPE_NAME).setId(entity.getId().toString()).setSource(classifiedAsBytes).setRefresh(true).execute().actionGet();
        }
    }

    @Override
    public void removeFromIndex(AbstractEntity entity) {
        if (entity instanceof Classified) {
            elasticSearch.prepareDelete(INDEX_NAME, CLASSIFIED_TYPE_NAME, entity.getId().toString()).setRefresh(true).execute().actionGet();
        }
    }

    @Override
    public void reIndexClassifieds() throws IOException {
        List<Classified> classifieds = baseDao.findAll(Classified.class);
        final IndicesAdminClient indicesAdminClient = elasticSearch.admin().indices();
        Map<String, Object> config = elasticSearchConfigResolver.resolveElasticsearchConfig("json");
        String indexRootName = SearchIndices.classifieds.toString();
        Map<String, Object> index = (Map<String, Object>) config.get(indexRootName);
        String newIndexName = dropCreateIndexCommand.execute(indicesAdminClient, indexRootName, index);

        // Bulk index
        final BulkRequestBuilder bulkRequestBuilder = elasticSearch.prepareBulk();
        for (Classified classified : classifieds) {
            byte[] classifiedAsBytes = classifiedToByteArrayConverter.convert(classified);
            final
            IndexRequestBuilder
                    indexRequestBuilder =
                    elasticSearch.prepareIndex(newIndexName, SearchTypes.classified.toString(),
                            classified.getId().toString()).setSource(classifiedAsBytes);
            bulkRequestBuilder.add(indexRequestBuilder);
        }

        final BulkResponse bulkResponse = bulkRequestBuilder.execute().actionGet();
        LoggerFactory.getLogger(SearchEngineImpl.class).info("Bulk index of {} classifieds took {} ms", classifieds.size(), bulkResponse.tookInMillis());

    }

}
