/**
 *
 */
package org.diveintojee.poc.business;

import org.diveintojee.poc.business.impl.FacadeImpl;
import org.diveintojee.poc.domain.Classified;
import org.diveintojee.poc.domain.business.Facade;
import org.diveintojee.poc.domain.business.Validator;
import org.diveintojee.poc.domain.exceptions.BusinessException;
import org.diveintojee.poc.domain.validation.ValidationContext;
import org.diveintojee.poc.persistence.search.SearchEngine;
import org.diveintojee.poc.persistence.store.BaseDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author louis.gueye@gmail.com
 */
@RunWith(MockitoJUnitRunner.class)
public class FacadeImplTest {

    @Mock
    private Validator validator;

    @Mock
    private BaseDao baseDao;

    @Mock
    private SearchEngine searchEngine;

    @InjectMocks
    private Facade underTest = new FacadeImpl();

    @Test
    public void createClassifiedShouldSucceed() throws Throwable {

        // Variables
        Classified classified;

        // Given
        classified = Mockito.mock(Classified.class);

        // When
        final Long id = 2L;
        Mockito.when(classified.getId()).thenReturn(id);
        Long result = this.underTest.createClassified(classified);

        // Then
        Mockito.verify(classified).getId();
        assertSame(id, result);
        Mockito.verify(this.validator).validate(classified, ValidationContext.CREATE);
        Mockito.verify(this.baseDao).persist(classified);
        Mockito.verifyNoMoreInteractions(this.validator, classified, this.baseDao);

    }

    @Test(expected = IllegalArgumentException.class)
    public void createClassifiedShouldThrowIllegalArgumentExceptionWithNullClassified() throws Throwable {

        // Given
        final Classified classified = null;

        // When
        this.underTest.createClassified(classified);

    }

    @Test
    public void deleteClassifiedShouldSucceed() {

        // Given
        final Long classifiedId = 5L;
        final Classified persistedClassified = mock(Classified.class);
        when(this.baseDao.get(Classified.class, classifiedId)).thenReturn(persistedClassified);

        // When
        this.underTest.deleteClassified(classifiedId);

        // Then
        verify(this.baseDao).get(Classified.class, classifiedId);
        verify(this.baseDao).delete(Classified.class, classifiedId);
        verifyNoMoreInteractions(persistedClassified, baseDao);

    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteClassifiedShouldThrowIllegalArgumentExceptionWithNullClassifiedId() {

        // Given
        final Long classifiedId = null;

        // When
        this.underTest.deleteClassified(classifiedId);

    }

    @Test
    public void findClassifiedByCriteriaShouldInvokePersistence() {

        // Given
        final Classified classified = Mockito.mock(Classified.class);

        // When
        this.underTest.findClassifiedsByCriteria(classified);

        // Then
        Mockito.verify(this.searchEngine).findClassifiedsByCriteria(classified);

    }

    @Test(expected = IllegalArgumentException.class)
    public void findClassifiedByCriteriaShouldThrowIllegalArgumentExceptionWithNullClassified() {

        // Given
        final Classified classified = null;

        // When
        this.underTest.findClassifiedsByCriteria(classified);

    }

    @Test
    public void readClassifiedShouldInvokePersistence() {

        // Given
        final Long classifiedId = 5L;
        final Classified classified = Mockito.mock(Classified.class);
        Mockito.when(this.baseDao.get(Classified.class, classifiedId)).thenReturn(classified);

        // When
        this.underTest.readClassified(classifiedId);

        // Then
        Mockito.verify(this.baseDao).get(Classified.class, classifiedId);

    }

    @Test(expected = BusinessException.class)
    public void readClassifiedShouldThrowBusinessExceptionWhenNotFound() {

        // Given
        final Long classifiedId = 5L;
        final Classified classified = null;
        Mockito.when(this.baseDao.get(Classified.class, classifiedId)).thenReturn(classified);

        // When
        this.underTest.readClassified(classifiedId);

        // Then
        Mockito.verify(this.baseDao).get(Classified.class, classifiedId);
        Mockito.verifyNoMoreInteractions(this.baseDao);

    }

    @Test(expected = IllegalArgumentException.class)
    public void readClassifiedShouldThrowIllegalArgumentExceptionWhithNullId() {
        this.underTest.readClassified(null);
    }

    @Test
    public void updateClassifiedShouldSucceed() {

        // Variables
        final Classified detached;
        final Long id;
        String title;
        String description;

        // Given
        detached = Mockito.mock(Classified.class);
        id = 5L;
        title = "my new title";
        description = "my new description";
        Classified persisted = Mockito.mock(Classified.class);
        Mockito.when(detached.getId()).thenReturn(id);
        Mockito.when(detached.getTitle()).thenReturn(title);
        Mockito.when(detached.getDescription()).thenReturn(description);
        Mockito.when(this.baseDao.get(Classified.class, id)).thenReturn(persisted);

        // When
        this.underTest.updateClassified(id, detached);

        // Then
        Mockito.verify(this.baseDao).get(Classified.class, id);
        Mockito.verify(detached).getTitle();
        Mockito.verify(detached).getDescription();
        Mockito.verify(persisted).setTitle(title);
        Mockito.verify(persisted).setDescription(description);
        Mockito.verify(this.validator).validate(persisted, ValidationContext.UPDATE);
        Mockito.verifyNoMoreInteractions(detached, persisted, this.baseDao);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateClassifiedShouldThrowIllegalArgumentExceptionWithNullClassifiedId() {

        // Given
        final Classified classified = new Classified();
        final Long classifiedId = null;

        // When
        this.underTest.updateClassified(classifiedId, classified);

    }

    @Test(expected = BusinessException.class)
    public void updateClassifiedShouldThrowBusinessExceptionWithNullPersistedInstance() {

        // Given
        final Classified classified = new Classified();
        final Long id = 4L;
        classified.setId(id);

        Mockito.when(this.baseDao.get(Matchers.eq(Classified.class), Matchers.eq(id))).thenReturn(null);

        // When
        this.underTest.updateClassified(id, classified);

    }

    @Test(expected = IllegalArgumentException.class)
    public void updateClassifiedShouldThrowIllegalArgumentExceptionWithNullClassified() {

        // Given
        final Classified classified = null;

        // When
        this.underTest.updateClassified(null, classified);

    }

}
