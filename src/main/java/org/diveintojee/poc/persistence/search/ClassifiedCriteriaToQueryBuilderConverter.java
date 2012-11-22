package org.diveintojee.poc.persistence.search;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import org.diveintojee.poc.domain.Classified;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author louis.gueye@gmail.com
 */
@Component(ClassifiedCriteriaToQueryBuilderConverter.BEAN_ID)
public class ClassifiedCriteriaToQueryBuilderConverter implements Converter<Classified, QueryBuilder> {

    public static final String BEAN_ID = "classifiedCriteriaToQueryBuilderConverter";

    public QueryBuilder convert(Classified source) {
        Map<String, Object> criteria = criteriaAsMap(source);
        QueryBuilder queryBuilder;
        if (noCriteria(criteria)) {
            queryBuilder = QueryBuilders.matchAllQuery();
        } else {
            queryBuilder = QueryBuilders.boolQuery();
            for (Map.Entry<String, Object> entry : criteria.entrySet()) {
                String field = entry.getKey();
                Object value = entry.getValue();
                QueryBuilder fieldQueryBuilder = resolveQueryBuilder(field, value);
                ((BoolQueryBuilder) queryBuilder).must(fieldQueryBuilder);
            }
        }
        return queryBuilder;
    }

    protected Map<String, Object> criteriaAsMap(Classified source) {

        if (source == null) return null;

        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();

        String title = source.getTitle();
        if (!Strings.isNullOrEmpty(title)) builder.put(ClassifiedSearchFieldsRegistry.TITLE, title);

        String description = source.getDescription();
        if (!Strings.isNullOrEmpty(description)) builder.put(ClassifiedSearchFieldsRegistry.DESCRIPTION, description);

        return builder.build();

    }

    protected QueryBuilder resolveQueryBuilder(String field, Object value) {

        if (ClassifiedSearchFieldsRegistry.TITLE.equals(field)
                || ClassifiedSearchFieldsRegistry.DESCRIPTION.equals(field))
            return QueryBuilders.queryString((String) value).field(field);

        throw new UnsupportedOperationException("No query builder resolved for field '" + field + "'");

    }

    protected boolean noCriteria(Map<String, Object> criteria) {
        return criteria == null || criteria.size() == 0;
    }
}
