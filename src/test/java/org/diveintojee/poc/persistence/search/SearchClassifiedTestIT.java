/**
 *
 */
package org.diveintojee.poc.persistence.search;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.diveintojee.poc.TestConstants;
import org.diveintojee.poc.TestFixtures;
import org.diveintojee.poc.domain.Classified;
import org.diveintojee.poc.persistence.search.factory.DropCreateIndexCommand;
import org.diveintojee.poc.persistence.search.factory.ElasticSearchConfigResolver;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.Map;

import static org.diveintojee.poc.persistence.search.SearchEngine.CLASSIFIEDS_ALIAS;
import static org.diveintojee.poc.persistence.search.SearchEngine.CLASSIFIED_TYPE;
import static org.junit.Assert.*;

/**
 * @author louis.gueye@gmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class})
@ContextConfiguration({TestConstants.SERVER_CONTEXT, TestConstants.SEARCH_CONTEXT_TEST})
public class SearchClassifiedTestIT {

    @Autowired
    @Qualifier(ClassifiedToJsonByteArrayConverter.BEAN_ID)
    private ClassifiedToJsonByteArrayConverter classifiedToJsonByteArrayConverter;

    @Autowired
    @Qualifier(JsonByteArrayToClassifiedConverter.BEAN_ID)
    private JsonByteArrayToClassifiedConverter jsonByteArrayToClassifiedConverter;

    @Autowired
    private Client underTest;

    @Autowired
    private ElasticSearchConfigResolver elasticSearchConfigResolver;

    @Autowired
    private DropCreateIndexCommand dropCreateIndexCommand;

    @Autowired
    private SearchEngine searchEngine;

    @Before
    public void configureSearchEngine() throws Exception {

        Map<String, Object> config = elasticSearchConfigResolver.getConfig();

        for (String indexRootName : config.keySet()) {
            if (!"settings".equalsIgnoreCase(indexRootName)) {
                Map<String, Object> index = (Map<String, Object>) config.get(indexRootName);
                dropCreateIndexCommand.execute(underTest.admin().indices(), indexRootName, index);
            }
        }
        //this.underTest.admin().cluster().prepareHealth(CLASSIFIEDS_ALIAS).setWaitForYellowStatus().execute().actionGet();
    }

    @Test
    public void findByDescriptionShouldSucceed() {
        final Long id = 8L;
        int expectedHitsCount;
        SearchResponse actualResponse;
        Classified classified;
        String description;
        String query;

        // Given I index that data
        description = "Gouts et saveurs";
        classified = TestFixtures.validClassified();
        classified.setDescription(description);
        indexClassified(id, classified);
        indexClassified(1L, TestFixtures.validClassified());

        // When I search
        query = "gouts";
        expectedHitsCount = 1;
        actualResponse = findByDescription(query);
        // Then I should get 1 hit
        assertHitsCount(expectedHitsCount, actualResponse);
        classified = extractClassifiedFromResponse(actualResponse);
        assertEquals(description, classified.getDescription());

        // When I search
        query = "goûts";
        expectedHitsCount = 1;
        actualResponse = findByDescription(query);
        // Then I should get 1 hit
        assertHitsCount(expectedHitsCount, actualResponse);
        classified = extractClassifiedFromResponse(actualResponse);
        assertEquals(description, classified.getDescription());

        // When I search
        query = "saveurs";
        expectedHitsCount = 1;
        actualResponse = findByDescription(query);
        // Then I should get 1 hit
        assertHitsCount(expectedHitsCount, actualResponse);
        classified = extractClassifiedFromResponse(actualResponse);
        assertEquals(description, classified.getDescription());
    }

    @Test
    public void findByTitleShouldSucceed() {
        final Long id = 8L;
        int expectedHitsCount;
        SearchResponse actualResponse;
        Classified classified;
        String title;
        String query;

        // Given I index that data
        title = "Gouts et saveurs";
        classified = TestFixtures.validClassified();
        classified.setTitle(title);
        indexClassified(id, classified);
        indexClassified(1L, TestFixtures.validClassified());

        // When I search
        query = "gouts";
        expectedHitsCount = 1;
        actualResponse = findByTitle(query);
        // Then I should get 1 hit
        assertHitsCount(expectedHitsCount, actualResponse);
        classified = extractClassifiedFromResponse(actualResponse);
        assertEquals(title, classified.getTitle());

        // When I search
        query = "goûts";
        expectedHitsCount = 1;
        actualResponse = findByTitle(query);
        // Then I should get 1 hit
        assertHitsCount(expectedHitsCount, actualResponse);
        classified = extractClassifiedFromResponse(actualResponse);
        assertEquals(title, classified.getTitle());

        // When I search
        query = "saveurs";
        expectedHitsCount = 1;
        actualResponse = findByTitle(query);
        // Then I should get 1 hit
        assertHitsCount(expectedHitsCount, actualResponse);
        classified = extractClassifiedFromResponse(actualResponse);
        assertEquals(title, classified.getTitle());
    }

    private SearchResponse findByTitle(final String query) {
        QueryStringQueryBuilder queryString = QueryBuilders.queryString(query).field(ClassifiedSearchFieldsRegistry.TITLE);
        return this.underTest.prepareSearch(SearchEngine.CLASSIFIEDS_ALIAS).setTypes(CLASSIFIED_TYPE).setQuery(queryString).execute().actionGet();
    }

    private SearchResponse findByDescription(final String query) {
        QueryStringQueryBuilder queryString = QueryBuilders.queryString(query).field(ClassifiedSearchFieldsRegistry.DESCRIPTION);
        return this.underTest.prepareSearch(CLASSIFIEDS_ALIAS).setTypes(CLASSIFIED_TYPE).setQuery(queryString).execute().actionGet();
    }

    /**
     * @param id
     * @param classified
     */
    private void indexClassified(final Long id, final Classified classified) {
        classified.setId(id);
        searchEngine.index(classified);
    }

    private void assertHitsCount(final int expectedHitsCount, final SearchResponse actualResponse) {

        assertNotNull(actualResponse);

        final SearchHits hits = actualResponse.getHits();

        assertNotNull(hits);

        final int totalHits = (int) hits.getTotalHits();
        assertTrue(expectedHitsCount == totalHits);

    }

    /**
     * @param actualResponse
     * @return
     */
    private Classified extractClassifiedFromResponse(final SearchResponse actualResponse) {

        assertNotNull(actualResponse);

        assertNotNull(actualResponse.getHits());

        final SearchHits hits = actualResponse.getHits();

        assertEquals(1, hits.getTotalHits());

        final SearchHit hit = hits.getHits()[0];

        assertNotNull(hit);

        assertNotNull(hit.source());

        final Classified advert = this.jsonByteArrayToClassifiedConverter.convert(hit.source());

        return advert;

    }

}
