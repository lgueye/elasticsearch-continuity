package org.diveintojee.poc.persistence.search;

import com.google.common.collect.Lists;
import org.diveintojee.poc.domain.Classified;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: lgueye Date: 17/09/12 Time: 17:44
 */
@RunWith(MockitoJUnitRunner.class)
public class SearchEngineImplTest {

    @Mock
    Client elasticsearch;

    @Mock
    private ClassifiedCriteriaToQueryBuilderConverter classifiedToQueryBuilderConverter;

    @Mock
    private SearchResponseToClassifiedsListConverter searchResponseToClassifiedsListConverter;

    @InjectMocks
    private SearchEngine underTest = new SearchEngineImpl();

    @Test
    public void findClassifiedsByCriteriaShouldSucceed() throws Exception {

        Classified criteria = mock(Classified.class);
        QueryBuilder queryBuilder = mock(QueryBuilder.class);
        when(classifiedToQueryBuilderConverter.convert(criteria)).thenReturn(queryBuilder);
        SearchRequestBuilder searchRequestBuilder = mock(SearchRequestBuilder.class);
        when(elasticsearch.prepareSearch(SearchEngine.INDEX_NAME)).thenReturn(searchRequestBuilder);
        when(searchRequestBuilder.setTypes(SearchEngine.CLASSIFIED_TYPE_NAME)).thenReturn(searchRequestBuilder);
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

}
