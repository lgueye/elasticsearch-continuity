package org.diveintojee.poc.persistence.search;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.diveintojee.poc.TestConstants;
import org.diveintojee.poc.persistence.search.factory.DropCreateIndicesCommand;
import org.diveintojee.poc.persistence.search.factory.IndexConfiguration;
import org.diveintojee.poc.persistence.search.factory.MappingConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * User: lgueye Date: 21/09/12 Time: 10:37
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({TestConstants.SERVER_CONTEXT, TestConstants.SEARCH_CONTEXT_TEST})
public class DropCreateIndicesCommandTestIT {

    @Autowired
    private DropCreateIndicesCommand underTest;

    @Test
    public void scanIndexConfigurationsITTestShouldSucceed() throws IOException {

        File rootFolder = new ClassPathResource("/testelasticsearchlayout").getFile();
        List<IndexConfiguration>
                indexConfigurations =
                underTest.scanIndexConfigurations(rootFolder, "json");
        assertNotNull(indexConfigurations);
        assertEquals(2, indexConfigurations.size());
        Collection<String>
                indexNames =
                Collections2.transform(indexConfigurations, new Function<IndexConfiguration, String>() {
                    @Override
                    public String apply(IndexConfiguration input) {
                        return input.getName();
                    }
                });
        assertEquals(2, indexNames.size());
        assertTrue(indexNames.contains("index1"));
        assertTrue(indexNames.contains("index2"));

        Collection<String>
                indexConfigLocations =
                Collections2.transform(indexConfigurations, new Function<IndexConfiguration, String>() {
                    @Override
                    public String apply(IndexConfiguration input) {
                        return input.getConfigLocation();
                    }
                });

        assertEquals(2, indexConfigLocations.size());

        for (String indexConfigLocation : indexConfigLocations) {
            assertTrue(Pattern.matches(".*index(\\d)/_settings\\.json$", indexConfigLocation));
        }

        Collection<IndexConfiguration>
                index1Configuration =
                Collections2.filter(indexConfigurations, new Predicate<IndexConfiguration>() {
                    @Override
                    public boolean apply(IndexConfiguration input) {
                        return "index1".equals(input.getName());

                    }

                });

        List<MappingConfiguration>
                index1MappingConfigurations =
                index1Configuration.iterator().next().getMappingConfigurations();
        Collection<String>
                index1Mappings =
                Collections2
                        .transform(index1MappingConfigurations, new Function<MappingConfiguration, String>() {
                            @Override
                            public String apply(MappingConfiguration input) {
                                return input.getType();
                            }
                        });

        assertEquals(3, index1Mappings.size());
        assertTrue(index1Mappings.contains("mapping11"));
        assertTrue(index1Mappings.contains("mapping12"));
        assertTrue(index1Mappings.contains("mapping13"));

        Collection<IndexConfiguration>
                index2Configuration =
                Collections2.filter(indexConfigurations, new Predicate<IndexConfiguration>() {
                    @Override
                    public boolean apply(IndexConfiguration input) {
                        return "index2".equals(input.getName());

                    }

                });

        List<MappingConfiguration>
                index2MappingConfigurations =
                index2Configuration.iterator().next().getMappingConfigurations();
        Collection<String>
                index2Mappings =
                Collections2
                        .transform(index2MappingConfigurations, new Function<MappingConfiguration, String>() {
                            @Override
                            public String apply(MappingConfiguration input) {
                                return input.getType();
                            }
                        });
        assertEquals(1, index2Mappings.size());
        assertTrue(index2Mappings.contains("mapping21"));

    }

}
