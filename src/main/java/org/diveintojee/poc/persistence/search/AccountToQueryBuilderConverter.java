package org.diveintojee.poc.persistence.search;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import org.diveintojee.poc.domain.Account;
import org.diveintojee.poc.domain.Restaurant;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * @author louis.gueye@gmail.com
 */
@Component(AccountToQueryBuilderConverter.BEAN_ID)
public class AccountToQueryBuilderConverter implements Converter<Account, QueryBuilder> {

    public static final String BEAN_ID = "AccountToQueryBuilderConverter";

    public QueryBuilder convert(Account source) {
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

    protected Map<String, Object> criteriaAsMap(Account source) {

        if (source == null) return null;

        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();

//        String name = source.getName();
//        if (!Strings.isNullOrEmpty(name)) builder.put(AccountSearchFieldsRegistry.NAME, name);
//
//        String description = source.getDescription();
//        if (!Strings.isNullOrEmpty(description)) builder.put(AccountSearchFieldsRegistry.DESCRIPTION, description);
//
//        String mainOffer = source.getMainOffer();
//        if (!Strings.isNullOrEmpty(mainOffer)) builder.put(AccountSearchFieldsRegistry.MAIN_OFFER, mainOffer);
//
//        String companyId = source.getCompanyId();
//        if (!Strings.isNullOrEmpty(companyId)) builder.put(AccountSearchFieldsRegistry.COMPANY_ID, companyId);
//
//        Boolean halal = source.isHalal();
//        if (halal != null) builder.put(AccountSearchFieldsRegistry.HALAL, halal);
//
//        Boolean kosher = source.isKosher();
//        if (kosher != null) builder.put(AccountSearchFieldsRegistry.KOSHER, kosher);
//
//        Boolean vegetarian = source.isVegetarian();
//        if (vegetarian != null) builder.put(AccountSearchFieldsRegistry.VEGETARIAN, vegetarian);

        return builder.build();

    }

    protected QueryBuilder resolveQueryBuilder(String field, Object value) {

        if (AccountSearchFieldsRegistry.NAME.equals(field)
                || AccountSearchFieldsRegistry.DESCRIPTION.equals(field)
                || AccountSearchFieldsRegistry.MAIN_OFFER.equals(field)
                || AccountSearchFieldsRegistry.STREET_ADDRESS.equals(field)
                || AccountSearchFieldsRegistry.CITY.equals(field)
                || AccountSearchFieldsRegistry.POSTAL_CODE.equals(field))
            return QueryBuilders.queryString((String) value).field(field);

        if (AccountSearchFieldsRegistry.COMPANY_ID.equals(field)
                || AccountSearchFieldsRegistry.KOSHER.equals(field)
                || AccountSearchFieldsRegistry.HALAL.equals(field)
                || AccountSearchFieldsRegistry.COUNTRY_CODE.equals(field)
                || AccountSearchFieldsRegistry.VEGETARIAN.equals(field))
            return QueryBuilders.termQuery(field, value);

        if (AccountSearchFieldsRegistry.SPECIALTIES.equals(field))
            return QueryBuilders.termsQuery(field + ".code", ((Collection<String>) value).toArray());

        throw new UnsupportedOperationException("No query builder resolved for field '" + field + "'");

    }

    protected boolean noCriteria(Map<String, Object> criteria) {

        return criteria == null || criteria.size() == 0;

    }
}
