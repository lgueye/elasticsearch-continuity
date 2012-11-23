package org.diveintojee.poc.persistence.search;

import org.diveintojee.poc.domain.AbstractEntity;
import org.diveintojee.poc.domain.Classified;
import org.diveintojee.poc.persistence.store.BaseDao;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
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
            byte[] classifiedAsBytes = classifiedToByteArrayConverter.convert((Classified) entity);
            elasticsearch.prepareIndex(INDEX_NAME, CLASSIFIED_TYPE_NAME).setId(entity.getId().toString()).setSource(classifiedAsBytes).setRefresh(true).execute().actionGet();
        }
    }

    @Override
    public void removeFromIndex(AbstractEntity entity) {
        if (entity instanceof Classified) {
            elasticsearch.prepareDelete(INDEX_NAME, CLASSIFIED_TYPE_NAME, entity.getId().toString()).setRefresh(true).execute().actionGet();
        }
    }

    @Override
    public void reIndexClassifieds() {
        List<Classified> classifieds = baseDao.findAll(Classified.class);
        final IndicesAdminClient indicesAdminClient = elasticsearch.admin().indices();

        final String currentIndexName = getCurrentIndexName(indicesAdminClient);

        final String newIndexName = getNewIndexName(currentIndexName);

        // Create index
        final CreateIndexResponse createIndexResponse = indicesAdminClient.prepareCreate(newIndexName).execute().actionGet();
        final boolean createIndexResponseAcknowledged = createIndexResponse.acknowledged();
        if (!createIndexResponseAcknowledged)
            throw new IllegalStateException("createIndexResponse acknowledged expected");

        // Put mapping
        String type = mappingConfiguration.getType();
        String mappingLocation = mappingConfiguration.getLocation();
        String mappingSource = fileHelper.fileContentAsString(mappingLocation);
        PutMappingResponse
                putMappingResponse =
                client.admin().indices().preparePutMapping(indexName).setSource(mappingSource).setType(type)
                        .execute().actionGet();
        if (!putMappingResponse.acknowledged()) {
            throw new RuntimeException(
                    "Failed to put mapping '" + type + "' for index '" + indexName + "'");
        }

        // Bulk index
        final BulkRequestBuilder bulkRequestBuilder = elasticsearch.prepareBulk();
        for (Classified classified : classifieds) {
            byte[] classifiedAsBytes = classifiedToByteArrayConverter.convert(classified);
            final
            IndexRequestBuilder
                    indexRequestBuilder =
                    elasticsearch.prepareIndex(newIndexName, SearchTypes.classified.toString(),
                            classified.getId().toString()).setSource(classifiedAsBytes);
            bulkRequestBuilder.add(indexRequestBuilder);
        }

        final BulkResponse bulkResponse = bulkRequestBuilder.execute().actionGet();
        LoggerFactory.getLogger(SearchEngineImpl.class).info("Bulk index of {} classifieds took {} ms", classifieds.size(), bulkResponse.tookInMillis());

        // Add new index to alias
        final IndicesAliasesResponse addIndicesAliasesResponse = indicesAdminClient.prepareAliases().addAlias(
                newIndexName, SearchIndices.classifieds.toString()).execute().actionGet();
        final boolean addIndicesAliasesResponseAcknowledged = addIndicesAliasesResponse.acknowledged();
        if (!addIndicesAliasesResponseAcknowledged)
            throw new IllegalStateException("addIndicesAliasesResponse acknowledged expected");

        // Remove old index from alias
        final
        IndicesAliasesResponse
                removeIndicesAliasesResponse =
                indicesAdminClient.prepareAliases()
                        .removeAlias(currentIndexName, SearchIndices.classifieds.toString()).execute().actionGet();
        final boolean removeIndicesAliasesResponseAcknowledged = removeIndicesAliasesResponse.acknowledged();
        if (!removeIndicesAliasesResponseAcknowledged)
            throw new IllegalStateException("removeIndicesAliasesResponse acknowledged expected");

        // Delete old index
        final
        DeleteIndexResponse
                deleteIndexResponse =
                indicesAdminClient.prepareDelete(currentIndexName).execute().actionGet();
        boolean deleteIndexResponseAcknowledged = deleteIndexResponse.acknowledged();
        if (!deleteIndexResponseAcknowledged)
            throw new IllegalStateException("deleteIndexResponse acknowledged expected");

    }

}
