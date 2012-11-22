/**
 *
 */
package org.diveintojee.poc.persistence.search;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.diveintojee.poc.TestConstants;
import org.diveintojee.poc.TestFixtures;
import org.diveintojee.poc.domain.Classified;
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

    private static final String INDEX_NAME = SearchIndices.classifieds.toString();
    private static final String TYPE_NAME = SearchTypes.classified.toString();

    @Value("classpath:/elasticsearch/classifieds/_settings.json")
    private Resource indexSettings;

    @Value("classpath:/elasticsearch/classifieds/classified.json")
    private Resource classifiedsMapping;

    @Autowired
    private Client underTest;

    @Before
    public void configureSearchEngine() throws Exception {

        // Deletes index if already exists
        if (this.underTest.admin().indices().prepareExists(INDEX_NAME).execute().actionGet().exists()) {
            DeleteIndexResponse deleteIndexResponse = this.underTest.admin().indices().prepareDelete(INDEX_NAME)
                    .execute().actionGet();
            deleteIndexResponse.acknowledged();
        }

        String indexSettingsAsString = Resources.toString(this.indexSettings.getURL(), Charsets.UTF_8);
        CreateIndexResponse createIndexResponse = this.underTest.admin().indices().prepareCreate(INDEX_NAME)
                .setSettings(indexSettingsAsString).execute().actionGet();
        createIndexResponse.acknowledged();

        String classifiedMappingAsString = Resources.toString(this.classifiedsMapping.getURL(), Charsets.UTF_8);
        PutMappingResponse putMappingResponse = this.underTest.admin().indices().preparePutMapping(INDEX_NAME)
                .setType(TYPE_NAME).setSource(classifiedMappingAsString).execute().actionGet();
        putMappingResponse.acknowledged();

        //this.underTest.admin().cluster().prepareHealth(INDEX_NAME).setWaitForYellowStatus().execute().actionGet();
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
        return this.underTest.prepareSearch(INDEX_NAME).setTypes(TYPE_NAME).setQuery(queryString).execute().actionGet();
    }

    private SearchResponse findByDescription(final String query) {
        QueryStringQueryBuilder queryString = QueryBuilders.queryString(query).field(ClassifiedSearchFieldsRegistry.DESCRIPTION);
        return this.underTest.prepareSearch(INDEX_NAME).setTypes(TYPE_NAME).setQuery(queryString).execute().actionGet();
    }

    /**
     * @param id
     * @param classified
     */
    private void indexClassified(final Long id, final Classified classified) {
        this.underTest.prepareIndex(INDEX_NAME, TYPE_NAME)//
                .setRefresh(true) //
                .setSource(this.classifiedToJsonByteArrayConverter.convert(classified)) //
                .execute().actionGet();
    }

    private void assertHitsCount(final int expectedHitsCount, final SearchResponse actualResponse) {

        assertNotNull(actualResponse);

        final SearchHits hits = actualResponse.getHits();

        assertNotNull(hits);

        final int totalHits = (int) hits.getTotalHits();
//        System.out.println("totalHits = " + totalHits);
//        System.out.println("expectedHitsCount = " + expectedHitsCount);
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
