package org.diveintojee.poc.persistence.search;

import com.google.common.collect.Lists;
import org.diveintojee.poc.domain.Classified;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author louis.gueye@gmail.com
 */
@Component(SearchResponseToClassifiedsListConverter.BEAN_ID)
public class SearchResponseToClassifiedsListConverter implements Converter<SearchResponse, List<Classified>> {

    public static final String BEAN_ID = "SearchResponseToClassifiedsListConverter";

    @Autowired
    private JsonByteArrayToClassifiedConverter byteArrayToClassifiedConverter;

    @Override
    public List<Classified> convert(SearchResponse source) {
        List<Classified> classifieds = Lists.newArrayList();
        if (source != null) {
            SearchHits searchHits = source.getHits();
            for (SearchHit searchHit : searchHits) {
                byte[] accountAsBytes = searchHit.source();
                Classified classified = byteArrayToClassifiedConverter.convert(accountAsBytes);
                classifieds.add(classified);
            }
        }
        return classifieds;
    }

}
