/*
 *
 */
package org.diveintojee.poc.business;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import junit.framework.Assert;

import org.apache.commons.collections.CollectionUtils;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.diveintojee.poc.TestConstants;
import org.diveintojee.poc.TestFixtures;
import org.diveintojee.poc.domain.Classified;
import org.diveintojee.poc.domain.business.Facade;
import org.diveintojee.poc.domain.exceptions.BusinessException;
import org.diveintojee.poc.persistence.search.SearchIndices;
import org.diveintojee.poc.persistence.search.SearchTypes;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.Client;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ResourceUtils;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.sql.DataSource;
import javax.validation.ConstraintViolationException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Facade integration testing<br/>
 *
 * @author louis.gueye@gmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {TestConstants.SERVER_CONTEXT,
        TestConstants.VALIDATION_CONTEXT, TestConstants.SEARCH_CONTEXT_TEST})
public class FacadeImplTestIT {

    @Autowired
    private Facade facade;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Client elasticsearch;

    @Value("classpath:/elasticsearch/classifieds/_settings.json")
    private Resource indexSettings;

    @Value("classpath:/elasticsearch/classifieds/classified.json")
    private Resource classifiedsMapping;

    private static final String INDEX_NAME = SearchIndices.classifieds.toString();
    private static final String TYPE_NAME = SearchTypes.classified.toString();

    @Before
    public void onSetUpInTransaction() throws Exception {
        final Connection con = DataSourceUtils.getConnection(this.dataSource);
        final IDatabaseConnection dbUnitCon = new DatabaseConnection(con);
        final IDataSet dataSet = new FlatXmlDataSetBuilder().build(ResourceUtils
                .getFile(TestConstants.PERSISTENCE_TEST_DATA));

        try {
            DatabaseOperation.CLEAN_INSERT.execute(dbUnitCon, dataSet);
        } finally {
            DataSourceUtils.releaseConnection(con, this.dataSource);
        }

        // Deletes index if already exists
        if (this.elasticsearch.admin().indices().prepareExists(INDEX_NAME).execute().actionGet().exists()) {
            DeleteIndexResponse deleteIndexResponse = this.elasticsearch.admin().indices().prepareDelete(INDEX_NAME)
                    .execute().actionGet();
            deleteIndexResponse.acknowledged();
        }

        String indexSettingsAsString = Resources
            .toString(this.indexSettings.getURL(), Charsets.UTF_8);
        CreateIndexResponse createIndexResponse = this.elasticsearch.admin().indices().prepareCreate(INDEX_NAME)
                .setSettings(indexSettingsAsString).execute().actionGet();
        if (!createIndexResponse.acknowledged()) throw new IllegalStateException();

        String classifiedMappingAsString = Resources.toString(this.classifiedsMapping.getURL(), Charsets.UTF_8);
        PutMappingResponse putMappingResponse = this.elasticsearch.admin().indices().preparePutMapping(INDEX_NAME)
                .setType(TYPE_NAME).setSource(classifiedMappingAsString).execute().actionGet();
      if (!putMappingResponse.acknowledged()) throw new IllegalStateException();
    }

    @Test
    public void createClassifiedShouldSucceed() throws Throwable {
        // Given
        final Classified classified = TestFixtures.validClassified();
        // ensure id nullity
        classified.setId(null);
        // When
        final Long id = this.facade.createClassified(classified);

        // Then
        Assert.assertNotNull(id);
        Assert.assertEquals(id, classified.getId());
        Assert.assertNotNull(this.facade.readClassified(id));

    }

    @Test
    public void updateClassifiedShouldSucceed() throws Throwable {
        // Given
        Classified classified = TestFixtures.validClassified();
        classified.setId(null);
        // When
        final Long id = this.facade.createClassified(classified);
        // Then
        Assert.assertNotNull(id);
        Assert.assertEquals(id, classified.getId());
        final String newTitle = "New Title";
        final String newDescription = "Brand New Description";

        // Given
        classified.setTitle(newTitle);
        classified.setDescription(newDescription);

        // When
        this.facade.updateClassified(id, classified);

        classified = this.facade.readClassified(id);

        // Then
        Assert.assertEquals(newTitle, classified.getTitle());
        Assert.assertEquals(newDescription, classified.getDescription());

    }

    @Test
    public void deleteClassifiedShouldSucceed() throws Throwable {
        // Given
        Classified classified = TestFixtures.validClassified();
        classified.setId(null);
        // When
        final Long id = this.facade.createClassified(classified);
        // Then
        Assert.assertNotNull(id);
        Assert.assertEquals(id, classified.getId());

        // When
        this.facade.deleteClassified(classified.getId());

        // Then
        try {
            this.facade.readClassified(id);
            fail(BusinessException.class.getName() + " expected");
        } catch (BusinessException e) {
            Assert.assertEquals("classified.not.found", e.getMessageCode());
        } catch (Throwable th) {
            fail(BusinessException.class.getName() + " expected, got " + th.getClass().getName());
        }
    }

    @Test
    public void findClassifiedByTitleShouldSucceed() throws Throwable {
        final Long id = 8L;
        int expectedHitsCount;
        List<Classified> actualResponse;
        Classified classified;
        String name;
        String query;
        Classified criteria;

        // Given I index that data
        expectedHitsCount = 1;
        name = "Gouts et saveurs";
        classified = TestFixtures.validClassified();
        classified.setTitle(name);
        facade.createClassified(TestFixtures.validClassified());

        // When I search
        query = "gouts";
        expectedHitsCount = 1;
        criteria = new Classified();
        criteria.setTitle(query);
        actualResponse = facade.findClassifiedsByCriteria(criteria);
        // Then I should get 1 hit
        assertEquals(expectedHitsCount, actualResponse.size());

        // When I search
        query = "goûts";
        expectedHitsCount = 1;
        criteria = new Classified();
        criteria.setTitle(query);
        actualResponse = facade.findClassifiedsByCriteria(criteria);
        // Then I should get 1 hit
        assertEquals(expectedHitsCount, actualResponse.size());

        // When I search
        query = "saveurs";
        expectedHitsCount = 1;
        criteria = new Classified();
        criteria.setTitle(query);
        actualResponse = facade.findClassifiedsByCriteria(criteria);
        // Then I should get 1 hit
        assertEquals(expectedHitsCount, actualResponse.size());
    }

    @Test
    public void findClassifiedByDescriptionShouldSucceed() throws Throwable {
        final Long id = 8L;
        int expectedHitsCount;
        List<Classified> actualResponse;
        Classified classified;
        String description;
        String query;
        Classified criteria;

        // Given I index that data
        description = "classified awesome description";
        classified = TestFixtures.validClassified();
        classified.setDescription(description);
        Long accountId = facade.createClassified(TestFixtures.validClassified());

        // When I search
        expectedHitsCount = 1;
        query = "awesome";
        criteria = new Classified();
        criteria.setDescription(query);
        actualResponse = facade.findClassifiedsByCriteria(criteria);
        // Then I should get 1 hit
        assertEquals(expectedHitsCount, actualResponse.size());

        // When I search
        expectedHitsCount = 1;
        query = "classified";
        criteria = new Classified();
        criteria.setDescription(query);
        actualResponse = facade.findClassifiedsByCriteria(criteria);
        // Then I should get 1 hit
        assertEquals(expectedHitsCount, actualResponse.size());

        // When I search
        expectedHitsCount = 1;
        query = "description";
        criteria = new Classified();
        criteria.setDescription(query);
        actualResponse = facade.findClassifiedsByCriteria(criteria);
        // Then I should get 1 hit
        assertEquals(expectedHitsCount, actualResponse.size());
    }

}
