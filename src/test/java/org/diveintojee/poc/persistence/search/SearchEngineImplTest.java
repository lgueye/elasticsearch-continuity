package org.diveintojee.poc.persistence.search;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.diveintojee.poc.domain.Classified;
import org.diveintojee.poc.persistence.search.factory.DropCreateIndexCommand;
import org.diveintojee.poc.persistence.search.factory.ElasticSearchConfigResolver;
import org.diveintojee.poc.persistence.store.BaseDao;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.diveintojee.poc.persistence.search.SearchEngine.CLASSIFIED_TYPE;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

/**
 * User: louis.gueye@gmail.com Date: 17/09/12 Time: 17:44
 */
@RunWith(MockitoJUnitRunner.class)
public class SearchEngineImplTest {

    @Mock
    private Client elasticSearch;

    @Mock
    private ElasticSearchConfigResolver elasticSearchConfigResolver;

    @Mock
    private ClassifiedCriteriaToQueryBuilderConverter classifiedToQueryBuilderConverter;

    @Mock
    private ClassifiedToJsonByteArrayConverter classifiedToByteArrayConverter;

    @Mock
    private SearchResponseToClassifiedsListConverter searchResponseToClassifiedsListConverter;

    @Mock
    private BaseDao baseDao;

    @Mock
    private DropCreateIndexCommand dropCreateIndexCommand;

    @InjectMocks
    private SearchEngine underTest = new SearchEngineImpl();

    @Test
    public void findClassifiedsByCriteriaShouldSucceed() throws Exception {

        Classified criteria = mock(Classified.class);
        QueryBuilder queryBuilder = mock(QueryBuilder.class);
        when(classifiedToQueryBuilderConverter.convert(criteria)).thenReturn(queryBuilder);
        SearchRequestBuilder searchRequestBuilder = mock(SearchRequestBuilder.class);
        when(elasticSearch.prepareSearch(SearchEngine.CLASSIFIEDS_ALIAS)).thenReturn(searchRequestBuilder);
        when(searchRequestBuilder.setTypes(CLASSIFIED_TYPE)).thenReturn(searchRequestBuilder);
        when(searchRequestBuilder.addSort(Matchers.<SortBuilder>any(SortBuilder.class))).thenReturn(searchRequestBuilder);
        when(searchRequestBuilder.setQuery(queryBuilder)).thenReturn(searchRequestBuilder);
        ListenableActionFuture<SearchResponse> actionFuture = mock(ListenableActionFuture.class);
        when(searchRequestBuilder.execute()).thenReturn(actionFuture);
        SearchResponse searchResponse = mock(SearchResponse.class);
        when(actionFuture.actionGet()).thenReturn(searchResponse);
        Classified r0 = mock(Classified.class);
        Classified r1 = mock(Classified.class);
        Classified r2 = mock(Classified.class);
        List<Classified> results = Lists.newArrayList(r0, r1, r2);
        when(searchResponseToClassifiedsListConverter.convert(searchResponse)).thenReturn(results);
        List<Classified> classifieds = underTest.findClassifiedsByCriteria(criteria);

        assertSame(classifieds, results);

    }

    @Test
    public void removeFromIndexShouldSucceed() {
        Map<String, Object> config = Maps.newHashMap();
        HashMap<String ,Object> indexConfig = Maps.newHashMap();
        config.put(SearchEngine.CLASSIFIEDS_ALIAS, indexConfig);
        String writeIndex = SearchEngine.CLASSIFIEDS_ALIAS + "-b";
        indexConfig.put("write-index", writeIndex);
        when(elasticSearchConfigResolver.getConfig()).thenReturn(config);
        Classified classified = mock(Classified.class);
        DeleteRequestBuilder deleteRequestBuilder = mock(DeleteRequestBuilder.class);
        Long id = 23L;
        when(classified.getId()).thenReturn(id);
        when(elasticSearch.prepareDelete(writeIndex, CLASSIFIED_TYPE, id.toString())).thenReturn(deleteRequestBuilder);
        when(deleteRequestBuilder.setRefresh(true)).thenReturn(deleteRequestBuilder);
        ListenableActionFuture<DeleteResponse> listenableActionFuture = mock(ListenableActionFuture.class);
        when(deleteRequestBuilder.execute()).thenReturn(listenableActionFuture);
        DeleteResponse deleteResponse = mock(DeleteResponse.class);
        when(listenableActionFuture.actionGet()).thenReturn(deleteResponse);
        underTest.removeFromIndex(classified);
        verify(elasticSearch).prepareDelete(writeIndex, CLASSIFIED_TYPE, id.toString());

    }

    @Test
    public void indexShouldSucceed() {
        Map<String, Object> config = Maps.newHashMap();
        HashMap<String ,Object> indexConfig = Maps.newHashMap();
        config.put(SearchEngine.CLASSIFIEDS_ALIAS, indexConfig);
        String writeIndex = SearchEngine.CLASSIFIEDS_ALIAS + "-b";
        indexConfig.put("write-index", writeIndex);
        when(elasticSearchConfigResolver.getConfig()).thenReturn(config);
        Classified classified = mock(Classified.class);
        IndexRequestBuilder indexRequestBuilder = mock(IndexRequestBuilder.class);
        Long id = 23L;
        when(classified.getId()).thenReturn(id);
        when(elasticSearch.prepareIndex(writeIndex, CLASSIFIED_TYPE, id.toString())).thenReturn(indexRequestBuilder);
        byte[] classifiedsAsBytes = "".getBytes();
        when(classifiedToByteArrayConverter.convert(classified)).thenReturn(classifiedsAsBytes);
        when(indexRequestBuilder.setSource(classifiedsAsBytes)).thenReturn(indexRequestBuilder);
        when(indexRequestBuilder.setRefresh(true)).thenReturn(indexRequestBuilder);
        ListenableActionFuture<IndexResponse> listenableActionFuture = mock(ListenableActionFuture.class);
        when(indexRequestBuilder.execute()).thenReturn(listenableActionFuture);
        IndexResponse indexResponse = mock(IndexResponse.class);
        when(listenableActionFuture.actionGet()).thenReturn(indexResponse);
        underTest.index(classified);
        verify(elasticSearch).prepareIndex(writeIndex, CLASSIFIED_TYPE, id.toString());

    }

    @Test
    public void reIndexClassifiedsShouldSucceed() throws IOException {
        Classified classified = mock(Classified.class);
        List<Classified> classifieds = Lists.newArrayList(classified);
        when(baseDao.findAll(Classified.class)).thenReturn(classifieds);
        AdminClient adminClient = mock(AdminClient.class);
        when(elasticSearch.admin()).thenReturn(adminClient);
        IndicesAdminClient indicesAdmin = mock(IndicesAdminClient.class);
        when(adminClient.indices()).thenReturn(indicesAdmin);
        Map<String, Object> config = Maps.newHashMap();
        HashMap<String, Object> indexConfig = Maps.newHashMap();
        config.put(SearchEngine.CLASSIFIEDS_ALIAS, indexConfig);
        String writeIndex = SearchEngine.CLASSIFIEDS_ALIAS + "-b";
        indexConfig.put("write-index", writeIndex);
        when(elasticSearchConfigResolver.getConfig()).thenReturn(config);
        BulkRequestBuilder bulkRequestBuilder = mock(BulkRequestBuilder.class);
        when(elasticSearch.prepareBulk()).thenReturn(bulkRequestBuilder);
        byte[] classifiedsAsBytes = "".getBytes();
        when(classifiedToByteArrayConverter.convert(classified)).thenReturn(classifiedsAsBytes);
        IndexRequestBuilder indexRequestBuilder = mock(IndexRequestBuilder.class);
        when(elasticSearch.prepareIndex(writeIndex, SearchEngine.CLASSIFIED_TYPE, classified.getId().toString())).thenReturn(indexRequestBuilder);
        when(indexRequestBuilder.setSource(classifiedsAsBytes)).thenReturn(indexRequestBuilder);
        ListenableActionFuture<BulkResponse> listenableActionFuture = mock(ListenableActionFuture.class);
        when(bulkRequestBuilder.execute()).thenReturn(listenableActionFuture);
        BulkResponse bulkResponse = mock(BulkResponse.class);
        when(listenableActionFuture.actionGet()).thenReturn(bulkResponse);
        when(bulkResponse.tookInMillis()).thenReturn(2L);

        underTest.reIndexClassifieds();

        verify(baseDao).findAll(Classified.class);
        verify(dropCreateIndexCommand).execute(indicesAdmin, SearchEngine.CLASSIFIEDS_ALIAS, indexConfig);
        verify(elasticSearch).prepareBulk();
        verify(classifiedToByteArrayConverter).convert(classified);
        verify(elasticSearch)
                .prepareIndex(writeIndex, SearchEngine.CLASSIFIED_TYPE, classified.getId().toString());
        verify(bulkRequestBuilder).add(indexRequestBuilder);
        verify(bulkRequestBuilder).execute();
        verify(listenableActionFuture).actionGet();
        verify(bulkResponse).tookInMillis();

    }

}
