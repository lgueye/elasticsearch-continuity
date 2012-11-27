/*
 *
 */
package org.diveintojee.poc.business;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import junit.framework.Assert;
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
import org.diveintojee.poc.persistence.search.factory.DropCreateIndexCommand;
import org.diveintojee.poc.persistence.search.factory.ElasticSearchConfigResolver;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.Client;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ResourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
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

    @Autowired
    private ElasticSearchConfigResolver elasticSearchConfigResolver;

    @Autowired
    private DropCreateIndexCommand dropCreateIndexCommand;

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

        Map<String, Object> config = elasticSearchConfigResolver.getConfig();

        for (String indexRootName : config.keySet()) {
            if (!"settings".equalsIgnoreCase(indexRootName)) {
                Map<String, Object> index = (Map<String, Object>) config.get(indexRootName);
                dropCreateIndexCommand.execute(elasticsearch.admin().indices(), indexRootName, index);
            }
        }

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
        String title;
        String query;
        Classified criteria;

        // Given I index that data
        expectedHitsCount = 1;
        title = "Gouts et saveurs";
        classified = TestFixtures.validClassified();
        classified.setTitle(title);
        facade.createClassified(classified);

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
        facade.createClassified(classified);

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
