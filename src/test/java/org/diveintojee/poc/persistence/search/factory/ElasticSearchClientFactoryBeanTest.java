package org.diveintojee.poc.persistence.search.factory;

import com.google.common.collect.Maps;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.node.NodeClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.BeanCreationException;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * @author louis.gueye@gmail.com
 */
@RunWith(MockitoJUnitRunner.class)
public class ElasticSearchClientFactoryBeanTest {

    @Mock
    private ElasticSearchConfigResolver elasticSearchConfigResolver;

    @InjectMocks
    private ElasticSearchClientFactoryBean underTest;

    @Test(expected = BeanCreationException.class)
    public void createInstanceShouldThrowBeanCreationExceptionWithNullTypology() throws Exception {
        String format = "json";
        underTest.setConfigFormat(format);
        underTest.createInstance();
    }

    @Test
    public void createInstanceShouldCreateNodeClient() throws Exception {
        String format = "json";
        underTest.setConfigFormat(format);
        underTest.setTypology(ElasticSearchClientTypology.local);
        Map<String, Object> config = Maps.newHashMap();
        String nodeSettings = "{\"node\":\"gandalf\"}";
        config.put("settings", nodeSettings);
        when(elasticSearchConfigResolver.getConfig()).thenReturn(config);
        Client client = underTest.createInstance();
        assertNotNull(client);
        assertTrue(client instanceof NodeClient);
        assertEquals("gandalf", ((NodeClient) client).settings().get("node"));
    }

    @Test(expected = BeanCreationException.class)
    public void createInstanceShouldThrowBeanCreationExceptionWithRemoteTypologyAndEmptyNodes() throws Exception {
        String format = "json";
        underTest.setConfigFormat(format);
        underTest.setTypology(ElasticSearchClientTypology.remote);
        underTest.createInstance();
    }

}
