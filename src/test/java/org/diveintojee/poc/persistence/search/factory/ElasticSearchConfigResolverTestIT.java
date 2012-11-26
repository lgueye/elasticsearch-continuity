package org.diveintojee.poc.persistence.search.factory;

import org.diveintojee.poc.TestConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * User: lgueye Date: 21/09/12 Time: 10:37
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({TestConstants.SERVER_CONTEXT, TestConstants.SEARCH_CONTEXT_TEST})
public class ElasticSearchConfigResolverTestIT {

    @Autowired
    private ElasticSearchConfigResolver underTest;

    @Test
    public void resolveIndicesConfigShouldSucceed() throws IOException {

        File rootFolder = new ClassPathResource("/testelasticsearchlayout").getFile();
        Map<String, Object> config =
                underTest.resolveIndicesConfig(rootFolder, "json");

        assertNotNull(config);
        assertEquals(2, config.size());
        Collection<String> indexNames = config.keySet();
        assertEquals(2, indexNames.size());
        assertTrue(indexNames.contains("index1"));
        assertTrue(indexNames.contains("index2"));

        Map<String, Object> index1 = (Map<String, Object>) config.get("index1");
        String index1SettingsAsString = (String) index1.get("settings");
        assertNotNull(index1SettingsAsString);
        Map<String, String> index1Mappings = (Map<String, String>) index1.get("mappings");
        assertNotNull(index1Mappings);
        assertEquals(3, index1Mappings.size());
        assertTrue(index1Mappings.keySet().contains("mapping11"));
        assertTrue(index1Mappings.keySet().contains("mapping12"));
        assertTrue(index1Mappings.keySet().contains("mapping13"));

        Map<String, Object> index2 = (Map<String, Object>) config.get("index2");
        String index2SettingsAsString = (String) index2.get("settings");
        assertNotNull(index2SettingsAsString);
        Map<String, String> index2Mappings = (Map<String, String>) index2.get("mappings");
        assertNotNull(index2Mappings);
        assertEquals(1, index2Mappings.size());
        assertTrue(index2Mappings.keySet().contains("mapping21"));

    }

}
