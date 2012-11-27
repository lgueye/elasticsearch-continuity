package org.diveintojee.poc.persistence.search;

import com.google.common.collect.Maps;
import org.diveintojee.poc.domain.Classified;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author louis.gueye@gmail.com
 */
public class ClassifiedCriteriaToQueryBuilderConverterTest {

    private ClassifiedCriteriaToQueryBuilderConverter underTest;

    @Before
    public void before() {
        underTest = new ClassifiedCriteriaToQueryBuilderConverter();
    }

    @Test
    public void noCriteriaShouldReturnTrueWithNullInput() {
        Map<String, Object> criteria = null;
        boolean noCriteria = underTest.noCriteria(criteria);
        assertTrue(noCriteria);
    }

    @Test
    public void noCriteriaShouldReturnTrueWithEmptyInput() {
        Map<String, Object> criteria = Maps.newHashMap();
        boolean noCriteria = underTest.noCriteria(criteria);
        assertTrue(noCriteria);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void resolveQueryBuilderShouldThrowUnsupportedOperationExceptionWithNullField() {
        String field = null;
        String value = "wathever";
        underTest.resolveQueryBuilder(field, value);

    }

    @Test(expected = UnsupportedOperationException.class)
    public void resolveQueryBuilderShouldThrowUnsupportedOperationExceptionWithEmptyField() {
        String field = "";
        String value = "wathever";
        underTest.resolveQueryBuilder(field, value);

    }

    @Test(expected = UnsupportedOperationException.class)
    public void resolveQueryBuilderShouldThrowUnsupportedOperationExceptionWithUnknownField() {
        String field = "whatever";
        String value = "wathever";
        underTest.resolveQueryBuilder(field, value);

    }

    @Test
    public void resolveQueryBuilderShouldResolveQueryStringBuilderForTitleField() {
        String field = ClassifiedSearchFieldsRegistry.TITLE;
        Object value = "whatever";
        QueryBuilder queryBuilder = underTest.resolveQueryBuilder(field, value);
        assertTrue(queryBuilder instanceof QueryStringQueryBuilder);
    }

    @Test
    public void resolveQueryBuilderShouldResolveQueryStringBuilderForDescriptionField() {
        String field = ClassifiedSearchFieldsRegistry.DESCRIPTION;
        Object value = "whatever";
        QueryBuilder queryBuilder = underTest.resolveQueryBuilder(field, value);
        assertTrue(queryBuilder instanceof QueryStringQueryBuilder);
    }

    @Test
    public void criteriaAsMapShouldReturnNullWithNullInput() {
        Classified source = null;
        Map<String, Object> criteria = underTest.criteriaAsMap(source);
        assertNull(criteria);
    }

    @Test
    public void criteriaAsMapShouldReturnEmptyMap() {
        Classified source = new Classified();
        Map<String, Object> criteria = underTest.criteriaAsMap(source);
        assertTrue(criteria.size() == 0);
    }

    @Test
    public void criteriaAsMapShouldAddName() {
        Classified source = new Classified();
        String title = "whatever";
        source.setTitle(title);
        Map<String, Object> criteria = underTest.criteriaAsMap(source);
        assertTrue(criteria.size() == 1);
        String key = ClassifiedSearchFieldsRegistry.TITLE;
        assertTrue(criteria.containsKey(key));
        assertEquals(title, criteria.get(key));
    }

    @Test
    public void criteriaAsMapShouldAddDescription() {
        Classified source = new Classified();
        String value = "whatever";
        source.setDescription(value);
        Map<String, Object> criteria = underTest.criteriaAsMap(source);
        assertTrue(criteria.size() == 1);
        String key = ClassifiedSearchFieldsRegistry.DESCRIPTION;
        assertTrue(criteria.containsKey(key));
        assertEquals(value, criteria.get(key));
    }

    @Test
    public void convertShouldResolveToMatchAllQueryBuilder() {
        Classified source;
        QueryBuilder queryBuilder;

        source = null;
        queryBuilder = underTest.convert(source);
        assertTrue(queryBuilder instanceof MatchAllQueryBuilder);

        source = new Classified();
        queryBuilder = underTest.convert(source);
        assertTrue(queryBuilder instanceof MatchAllQueryBuilder);
    }

}
