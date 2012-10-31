package org.diveintojee.poc.persistence.search;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;

import org.apache.commons.collections.CollectionUtils;
import org.diveintojee.poc.domain.Restaurant;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import fr.midipascher.domain.Address;
import fr.midipascher.domain.FoodSpecialty;
import fr.midipascher.domain.Restaurant;

/**
 * @author louis.gueye@gmail.com
 */
@Component(RestaurantToQueryBuilderConverter.BEAN_ID)
public class RestaurantToQueryBuilderConverter implements Converter<Restaurant, QueryBuilder> {

    public static final String BEAN_ID = "RestaurantToQueryBuilderConverter";

    public QueryBuilder convert(Restaurant source) {
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

    protected Map<String, Object> criteriaAsMap(Restaurant source) {

        if (source == null) return null;

        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();

        String name = source.getName();
        if (!Strings.isNullOrEmpty(name)) builder.put(RestaurantSearchFieldsRegistry.NAME, name);

        String description = source.getDescription();
        if (!Strings.isNullOrEmpty(description)) builder.put(RestaurantSearchFieldsRegistry.DESCRIPTION, description);

        String mainOffer = source.getMainOffer();
        if (!Strings.isNullOrEmpty(mainOffer)) builder.put(RestaurantSearchFieldsRegistry.MAIN_OFFER, mainOffer);

        String companyId = source.getCompanyId();
        if (!Strings.isNullOrEmpty(companyId)) builder.put(RestaurantSearchFieldsRegistry.COMPANY_ID, companyId);

        Boolean halal = source.isHalal();
        if (halal != null) builder.put(RestaurantSearchFieldsRegistry.HALAL, halal);

        Boolean kosher = source.isKosher();
        if (kosher != null) builder.put(RestaurantSearchFieldsRegistry.KOSHER, kosher);

        Boolean vegetarian = source.isVegetarian();
        if (vegetarian != null) builder.put(RestaurantSearchFieldsRegistry.VEGETARIAN, vegetarian);

        return builder.build();

    }

    protected QueryBuilder resolveQueryBuilder(String field, Object value) {

        if (RestaurantSearchFieldsRegistry.NAME.equals(field)
                || RestaurantSearchFieldsRegistry.DESCRIPTION.equals(field)
                || RestaurantSearchFieldsRegistry.MAIN_OFFER.equals(field)
                || RestaurantSearchFieldsRegistry.STREET_ADDRESS.equals(field)
                || RestaurantSearchFieldsRegistry.CITY.equals(field)
                || RestaurantSearchFieldsRegistry.POSTAL_CODE.equals(field))
            return QueryBuilders.queryString((String) value).field(field);

        if (RestaurantSearchFieldsRegistry.COMPANY_ID.equals(field)
                || RestaurantSearchFieldsRegistry.KOSHER.equals(field)
                || RestaurantSearchFieldsRegistry.HALAL.equals(field)
                || RestaurantSearchFieldsRegistry.COUNTRY_CODE.equals(field)
                || RestaurantSearchFieldsRegistry.VEGETARIAN.equals(field))
            return QueryBuilders.termQuery(field, value);

        if (RestaurantSearchFieldsRegistry.SPECIALTIES.equals(field))
            return QueryBuilders.termsQuery(field + ".code", ((Collection<String>) value).toArray());

        throw new UnsupportedOperationException("No query builder resolved for field '" + field + "'");

    }

    protected boolean noCriteria(Map<String, Object> criteria) {

        return criteria == null || criteria.size() == 0;

    }
}
