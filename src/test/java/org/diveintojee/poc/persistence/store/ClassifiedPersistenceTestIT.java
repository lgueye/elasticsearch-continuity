/*
 *
 */
package org.diveintojee.poc.persistence.store;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.diveintojee.poc.TestFixtures;
import org.diveintojee.poc.domain.Classified;
import org.diveintojee.poc.persistence.BasePersistenceTestIT;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Classified database integration testing<br/> CRUD operations are tested<br> Finders are
 * tested<br/>
 *
 * @author louis.gueye@gmail.com
 */
public class ClassifiedPersistenceTestIT extends BasePersistenceTestIT {

    /**
     * @param result
     * @param ids
     */
    private void assertResultContainsClassifiedIds(final List<Classified> result,
                                                   final Set<Long> ids) {
        if (CollectionUtils.isEmpty(result) && ids == null) {
            return;
        }

        final Set<Long> classifiedIds = new HashSet<Long>();
        for (final Classified classified : result) {
            classifiedIds.add(classified.getId());
        }

        Assert.assertTrue(classifiedIds.containsAll(ids));
    }

    /**
     * Given : a valid classified<br/> When : one persists the above classified<br/> Then : system
     * should retrieve it in database<br/>
     */
    @Test
    public void shouldCreateClassified() {
        // Given
        final Classified classified = TestFixtures.validClassified();

        // When
        baseDao.persist(classified);
        baseDao.flush();

        // Then
        Assert.assertNotNull(classified.getId());
        Assert.assertEquals(classified, baseDao.get(Classified.class, classified.getId()));
    }

    /**
     * Given : a valid classified<br/> When : one persists the above classified and then delete
     * it<br/> Then : system should not retrieve it in database<br/>
     */
    @Test
    public void shouldDeleteClassified() {
        // Given
        final Classified classified = TestFixtures.validClassified();

        // When
        baseDao.persist(classified);
        baseDao.flush();
        baseDao.delete(Classified.class, classified.getId());
        baseDao.flush();

        // Then
        final Classified persistedClassified = baseDao.get(Classified.class, classified.getId());
        Assert.assertNull(persistedClassified);
    }

    /**
     * Given : a valid classified<br/> When : one updates that classified<br/> Then : system should
     * persist changes<br/>
     */
    @Test
    public void shouldUpdateClassified() {
        // Given
        final Classified classified = TestFixtures.validClassified();

        baseDao.persist(classified);
        baseDao.flush();
        baseDao.evict(classified);
        final String newTitle = RandomStringUtils.random(Classified.CONSTRAINT_TITLE_MAX_SIZE);
        classified.setTitle(newTitle);

        // When
        baseDao.merge(classified);
        baseDao.flush();
        final Classified persistedClassified = baseDao.get(Classified.class, classified.getId());

        // Then
        Assert.assertEquals(classified.getTitle(), persistedClassified.getTitle());
    }

}
